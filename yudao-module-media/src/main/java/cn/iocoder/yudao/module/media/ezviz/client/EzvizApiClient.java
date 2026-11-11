package cn.iocoder.yudao.module.media.ezviz.client;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 萤石开放平台 (Ezviz Open Cloud) API 客户端
 *
 * @author MzMedia
 */
@Slf4j
@Component
public class EzvizApiClient {

    private static final String BASE_URL = "https://open.ys7.com";

    private final OkHttpClient httpClient;
    private final Map<String, TokenCache> tokenMap = new ConcurrentHashMap<>();

    private static class TokenCache {
        String token;
        long expireAt;
    }

    public EzvizApiClient() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 获取或刷新 AccessToken
     */
    public String getAccessToken(String appKey, String appSecret) {
        String cacheKey = appKey + ":" + appSecret;
        TokenCache cache = tokenMap.get(cacheKey);
        if (cache != null && System.currentTimeMillis() < cache.expireAt - 60000) {
            return cache.token;
        }

        RequestBody formBody = new FormBody.Builder()
                .add("appKey", appKey)
                .add("appSecret", appSecret)
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/lapp/token/get")
                .post(formBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String str = response.body().string();
                JSONObject json = JSON.parseObject(str);
                if ("200".equals(json.getString("code"))) {
                    JSONObject data = json.getJSONObject("data");
                    String token = data.getString("accessToken");
                    long expireTime = data.getLongValue("expireTime");

                    TokenCache tc = new TokenCache();
                    tc.token = token;
                    tc.expireAt = expireTime > 0 ? expireTime : System.currentTimeMillis() + 6 * 24 * 3600 * 1000L;
                    tokenMap.put(cacheKey, tc);
                    return token;
                } else {
                    log.warn("[萤石云] 获取Token返回错误: {}", str);
                }
            }
        } catch (IOException e) {
            log.error("[萤石云] 请求Token网络异常: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 获取直播播放地址 (HTTP-FLV / HLS / RTMP)
     */
    public String getLiveAddress(String appKey, String appSecret, String deviceSerial, int channelNo) {
        String token = getAccessToken(appKey, appSecret);
        if (token == null) return null;

        RequestBody formBody = new FormBody.Builder()
                .add("accessToken", token)
                .add("deviceSerial", deviceSerial)
                .add("channelNo", String.valueOf(channelNo > 0 ? channelNo : 1))
                .add("protocol", "4") // 4: FLV, 2: HLS
                .add("quality", "1")  // 1: 高清
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/lapp/v2/live/address/get")
                .post(formBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String str = response.body().string();
                JSONObject json = JSON.parseObject(str);
                if ("200".equals(json.getString("code"))) {
                    JSONObject data = json.getJSONObject("data");
                    return data.getString("url");
                }
            }
        } catch (Exception e) {
            log.warn("[萤石云] 获取直播地址异常: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 云台控制: 开始移动
     * direction: 0-上, 1-下, 2-左, 3-右, 4-左上, 5-左下, 6-右上, 7-右下, 8-放大, 9-缩小
     * speed: 1-慢速, 2-中速
     */
    public boolean ptzStart(String appKey, String appSecret, String deviceSerial, int channelNo, int direction, int speed) {
        String token = getAccessToken(appKey, appSecret);
        if (token == null) return false;

        RequestBody formBody = new FormBody.Builder()
                .add("accessToken", token)
                .add("deviceSerial", deviceSerial)
                .add("channelNo", String.valueOf(channelNo > 0 ? channelNo : 1))
                .add("direction", String.valueOf(direction))
                .add("speed", String.valueOf(speed > 0 ? speed : 1))
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/lapp/device/ptz/start")
                .post(formBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JSONObject json = JSON.parseObject(response.body().string());
                return "200".equals(json.getString("code"));
            }
        } catch (Exception e) {
            log.warn("[萤石云] PTZ Start异常: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 云台控制: 停止移动
     */
    public boolean ptzStop(String appKey, String appSecret, String deviceSerial, int channelNo, int direction) {
        String token = getAccessToken(appKey, appSecret);
        if (token == null) return false;

        RequestBody formBody = new FormBody.Builder()
                .add("accessToken", token)
                .add("deviceSerial", deviceSerial)
                .add("channelNo", String.valueOf(channelNo > 0 ? channelNo : 1))
                .add("direction", String.valueOf(direction))
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/lapp/device/ptz/stop")
                .post(formBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JSONObject json = JSON.parseObject(response.body().string());
                return "200".equals(json.getString("code"));
            }
        } catch (Exception e) {
            log.warn("[萤石云] PTZ Stop异常: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 抓拍设备图片
     */
    public String capturePictureUrl(String appKey, String appSecret, String deviceSerial, int channelNo) {
        String token = getAccessToken(appKey, appSecret);
        if (token == null) return null;

        RequestBody formBody = new FormBody.Builder()
                .add("accessToken", token)
                .add("deviceSerial", deviceSerial)
                .add("channelNo", String.valueOf(channelNo > 0 ? channelNo : 1))
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/lapp/device/capture")
                .post(formBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JSONObject json = JSON.parseObject(response.body().string());
                if ("200".equals(json.getString("code"))) {
                    return json.getJSONObject("data").getString("picUrl");
                }
            }
        } catch (Exception e) {
            log.warn("[萤石云] 抓拍异常: {}", e.getMessage());
        }
        return null;
    }
}
