package cn.iocoder.yudao.module.media.dahua.client;

import cn.iocoder.yudao.module.media.dahua.bean.DahuaDevice;
import com.burgstaller.okhttp.AuthenticationCacheInterceptor;
import com.burgstaller.okhttp.CachingAuthenticatorDecorator;
import com.burgstaller.okhttp.digest.CachingAuthenticator;
import com.burgstaller.okhttp.digest.Credentials;
import com.burgstaller.okhttp.digest.DigestAuthenticator;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 大华 HTTP RPC / CGI 客户端
 * 基于 Digest 鉴权，实现设备信息查询、RTSP流地址拼装、云台CGI控制及快照抓图
 *
 * @author MzMedia
 */
@Slf4j
@Component
public class DahuaRpcClient {

    private final Map<String, OkHttpClient> clientCache = new ConcurrentHashMap<>();

    private OkHttpClient getClient(DahuaDevice device) {
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
     * 获取大华设备系统信息
     */
    public boolean fetchSystemInfo(DahuaDevice device) {
        String url = "http://" + device.getIp() + ":" + device.getPort() + "/cgi-bin/magicBox.cgi?action=getSystemInfo";
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = getClient(device).newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String text = response.body().string();
                for (String line : text.split("\n")) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String k = parts[0].trim();
                        String v = parts[1].trim();
                        if (k.equalsIgnoreCase("deviceType")) device.setModel(v);
                        if (k.equalsIgnoreCase("serialNumber")) device.setSerialNumber(v);
                    }
                }
                device.setStatus(1);
                return true;
            }
        } catch (Exception e) {
            log.warn("[大华 CGI] 获取系统信息失败 {}: {}", url, e.getMessage());
        }
        return false;
    }

    /**
     * 获取通道 RTSP 播放地址
     * 大华标准格式: rtsp://username:password@ip:554/cam/realmonitor?channel=1&subtype=0
     */
    public String getRtspStreamUrl(DahuaDevice device, int channelNo, int subtype) {
        return String.format("rtsp://%s:%s@%s:554/cam/realmonitor?channel=%d&subtype=%d",
                device.getUsername(), device.getPassword(), device.getIp(), channelNo > 0 ? channelNo : 1, subtype);
    }

    /**
     * 云台控制
     * code: Up, Down, Left, Right, LeftUp, RightUp, LeftDown, RightDown, ZoomAdd, ZoomDec
     * action: start 或 stop
     */
    public boolean ptzAction(DahuaDevice device, int channelNo, String action, String code, int speed) {
        String url = String.format("http://%s:%d/cgi-bin/ptz.cgi?action=%s&channel=%d&code=%s&arg1=0&arg2=%d&arg3=0",
                device.getIp(), device.getPort(), action, channelNo > 0 ? channelNo : 1, code, Math.min(Math.max(speed, 1), 8));
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = getClient(device).newCall(request).execute()) {
            return response.isSuccessful();
        } catch (Exception e) {
            log.warn("[大华 CGI] PTZ动作失败 {}: {}", url, e.getMessage());
            return false;
        }
    }

    /**
     * 调用预置位
     */
    public boolean gotoPreset(DahuaDevice device, int channelNo, int presetIndex) {
        String url = String.format("http://%s:%d/cgi-bin/ptz.cgi?action=start&channel=%d&code=GotoPreset&arg1=0&arg2=%d&arg3=0",
                device.getIp(), device.getPort(), channelNo > 0 ? channelNo : 1, presetIndex);
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = getClient(device).newCall(request).execute()) {
            return response.isSuccessful();
        } catch (Exception e) {
            log.warn("[大华 CGI] 调用预置位失败 {}: {}", url, e.getMessage());
            return false;
        }
    }

    /**
     * 快照抓图
     */
    public byte[] capturePicture(DahuaDevice device, int channelNo) {
        String url = String.format("http://%s:%d/cgi-bin/snapshot.cgi?channel=%d",
                device.getIp(), device.getPort(), channelNo > 0 ? channelNo : 1);
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = getClient(device).newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().bytes();
            }
        } catch (Exception e) {
            log.warn("[大华 CGI] 抓拍失败 {}: {}", url, e.getMessage());
        }
        return null;
    }
}
