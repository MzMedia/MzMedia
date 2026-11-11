package cn.iocoder.yudao.module.media.hikvision.client;

import cn.iocoder.yudao.module.media.hikvision.bean.HikDevice;
import com.burgstaller.okhttp.AuthenticationCacheInterceptor;
import com.burgstaller.okhttp.CachingAuthenticatorDecorator;
import com.burgstaller.okhttp.digest.CachingAuthenticator;
import com.burgstaller.okhttp.digest.Credentials;
import com.burgstaller.okhttp.digest.DigestAuthenticator;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 海康威视 ISAPI HTTP REST 客户端
 * 基于 Digest 摘要认证，实现设备信息查询、通道发现、RTSP取流、PTZ控制与抓图
 *
 * @author MzMedia
 */
@Slf4j
@Component
public class HikIsapiClient {

    private final Map<String, OkHttpClient> clientCache = new ConcurrentHashMap<>();

    private OkHttpClient getClient(HikDevice device) {
        String key = device.getIp() + ":" + device.getPort() + ":" + device.getUsername();
        return clientCache.computeIfAbsent(key, k -> {
            Map<String, CachingAuthenticator> authCache = new ConcurrentHashMap<>();
            Credentials credentials = new Credentials(device.getUsername(), device.getPassword());
            DigestAuthenticator digestAuthenticator = new DigestAuthenticator(credentials);

            return new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(8, TimeUnit.SECONDS)
                    .authenticator(new CachingAuthenticatorDecorator(digestAuthenticator, authCache))
                    .addInterceptor(new AuthenticationCacheInterceptor(authCache))
                    .build();
        });
    }

    /**
     * 查询海康设备基础信息
     */
    public boolean fetchDeviceInfo(HikDevice device) {
        String url = "http://" + device.getIp() + ":" + device.getPort() + "/ISAPI/System/deviceInfo";
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = getClient(device).newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String xml = response.body().string();
                String model = extractTagValue(xml, "model");
                String serialNumber = extractTagValue(xml, "serialNumber");
                String firmware = extractTagValue(xml, "firmwareVersion");
                if (model != null) device.setModel(model);
                if (serialNumber != null) device.setSerialNumber(serialNumber);
                if (firmware != null) device.setFirmware(firmware);
                device.setStatus(1);
                return true;
            }
        } catch (Exception e) {
            log.warn("[海康 ISAPI] 获取设备信息失败 {}: {}", url, e.getMessage());
        }
        return false;
    }

    /**
     * 获取通道 RTSP 播放地址
     * 海康默认通道 1 主码流对应: rtsp://username:password@ip:554/Streaming/Channels/101
     * 子码流对应: rtsp://username:password@ip:554/Streaming/Channels/102
     */
    public String getRtspStreamUrl(HikDevice device, int channelNo, int streamType) {
        int streamIndex = channelNo * 100 + (streamType == 1 ? 2 : 1);
        return String.format("rtsp://%s:%s@%s:554/Streaming/Channels/%d",
                device.getUsername(), device.getPassword(), device.getIp(), streamIndex);
    }

    /**
     * 云台 PTZ 控制
     *
     * @param channelNo 通道号 (通常 1)
     * @param pan       水平方向 (1: 右, -1: 左, 0: 停止)
     * @param tilt      垂直方向 (1: 上, -1: 下, 0: 停止)
     * @param zoom      变倍 (1: 放大, -1: 缩小, 0: 停止)
     * @param speed     速度 (1-100)
     */
    public boolean ptzControl(HikDevice device, int channelNo, int pan, int tilt, int zoom, int speed) {
        String url = "http://" + device.getIp() + ":" + device.getPort() + "/ISAPI/PTZCtrl/channels/" + channelNo + "/continuous";

        int panSpeed = pan * speed;
        int tiltSpeed = tilt * speed;
        int zoomSpeed = zoom * speed;

        String xmlPayload = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<PTZData xmlns=\"http://www.isapi.org/ver20/XMLSchema\">\n" +
                "  <pan>" + panSpeed + "</pan>\n" +
                "  <tilt>" + tiltSpeed + "</tilt>\n" +
                "  <zoom>" + zoomSpeed + "</zoom>\n" +
                "</PTZData>";

        RequestBody body = RequestBody.create(xmlPayload, MediaType.parse("application/xml; charset=utf-8"));
        Request request = new Request.Builder().url(url).put(body).build();

        try (Response response = getClient(device).newCall(request).execute()) {
            return response.isSuccessful();
        } catch (Exception e) {
            log.warn("[海康 ISAPI] PTZ控制失败 {}: {}", url, e.getMessage());
            return false;
        }
    }

    /**
     * 调用预置位
     */
    public boolean gotoPreset(HikDevice device, int channelNo, int presetIndex) {
        String url = "http://" + device.getIp() + ":" + device.getPort() + "/ISAPI/PTZCtrl/channels/" + channelNo + "/presets/" + presetIndex + "/goto";
        RequestBody body = RequestBody.create(new byte[0], null);
        Request request = new Request.Builder().url(url).put(body).build();
        try (Response response = getClient(device).newCall(request).execute()) {
            return response.isSuccessful();
        } catch (Exception e) {
            log.warn("[海康 ISAPI] 调用预置位失败 {}: {}", url, e.getMessage());
            return false;
        }
    }

    /**
     * 实时抓取现场高清图片
     */
    public byte[] capturePicture(HikDevice device, int channelNo) {
        String url = "http://" + device.getIp() + ":" + device.getPort() + "/ISAPI/Streaming/channels/" + (channelNo * 100 + 1) + "/picture";
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = getClient(device).newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().bytes();
            }
        } catch (Exception e) {
            log.warn("[海康 ISAPI] 抓拍失败 {}: {}", url, e.getMessage());
        }
        return null;
    }

    private String extractTagValue(String xml, String tagName) {
        if (xml == null) return null;
        Pattern pattern = Pattern.compile("<" + tagName + ">([^<]+)</" + tagName + ">");
        Matcher matcher = pattern.matcher(xml);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }
}
