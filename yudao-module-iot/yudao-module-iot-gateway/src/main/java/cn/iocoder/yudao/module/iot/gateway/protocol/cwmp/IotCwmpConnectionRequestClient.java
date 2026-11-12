package cn.iocoder.yudao.module.iot.gateway.protocol.cwmp;

import cn.hutool.core.util.StrUtil;
import com.burgstaller.okhttp.AuthenticationCacheInterceptor;
import com.burgstaller.okhttp.CachingAuthenticatorDecorator;
import com.burgstaller.okhttp.digest.CachingAuthenticator;
import com.burgstaller.okhttp.digest.Credentials;
import com.burgstaller.okhttp.digest.DigestAuthenticator;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TR-069 ACS 主动唤醒客户端 (Connection Request)
 *
 * 向 CPE 的 ConnectionRequestURL 发送带有 HTTP Digest 认证的 GET 请求，触发 CPE 立即回连 ACS
 *
 * @author 芋道源码
 */
@Slf4j
public class IotCwmpConnectionRequestClient {

    private final OkHttpClient defaultClient;

    public IotCwmpConnectionRequestClient() {
        this.defaultClient = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(10))
                .followRedirects(true)
                .build();
    }

    /**
     * 发送 Connection Request 唤醒 CPE
     *
     * @param connectionRequestUrl CPE 的 ConnectionRequestURL
     * @param username 认证用户名（可为空）
     * @param password 认证密码（可为空）
     * @return 是否成功唤醒
     */
    public boolean trigger(String connectionRequestUrl, String username, String password) {
        if (StrUtil.isBlank(connectionRequestUrl)) {
            log.warn("[trigger][ConnectionRequestURL 为空，无法发起唤醒]");
            return false;
        }

        log.info("[trigger][正在向 CPE 发送 Connection Request: {}, 用户名: {}]", connectionRequestUrl, username);

        OkHttpClient client = defaultClient;

        // 如果配置了 Digest 用户名密码，配置 Digest Authenticator
        if (StrUtil.isNotBlank(username) && StrUtil.isNotBlank(password)) {
            try {
                DigestAuthenticator authenticator = new DigestAuthenticator(new Credentials(username, password));
                Map<String, CachingAuthenticator> authCache = new ConcurrentHashMap<>();
                client = defaultClient.newBuilder()
                        .authenticator(new CachingAuthenticatorDecorator(authenticator, authCache))
                        .addInterceptor(new AuthenticationCacheInterceptor(authCache))
                        .build();
            } catch (Exception e) {
                log.warn("[trigger][构建 Digest 认证失败，使用默认客户端重试: {}]", e.getMessage());
            }
        }

        Request request = new Request.Builder()
                .url(connectionRequestUrl)
                .get()
                .header("User-Agent", "MzMedia-CWMP-ACS/1.0")
                .build();

        try (Response response = client.newCall(request).execute()) {
            int code = response.code();
            log.info("[trigger][CPE Connection Request 响应码: {}]", code);
            // 200 OK 或 204 No Content 代表成功接收唤醒
            return code == 200 || code == 204;
        } catch (Exception e) {
            log.error("[trigger][向 CPE {} 发起 Connection Request 失败: {}]", connectionRequestUrl, e.getMessage());
            return false;
        }
    }

}
