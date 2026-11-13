package cn.iocoder.yudao.module.media.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 视频流媒体模块核心统一配置属性类
 *
 * 统一收拢前缀：mz.video
 *
 * @author MzMedia Architect
 */
@Data
@Component
@ConfigurationProperties(prefix = "mz.video")
public class MzVideoProperties {

    /**
     * GB28181 SIP 信令服务配置
     */
    private SipProperties sip = new SipProperties();

    /**
     * ZLMediaKit 流媒体服务集群配置
     */
    private MediaProperties media = new MediaProperties();

    /**
     * 云端录像与文件存储配置
     */
    private RecordProperties record = new RecordProperties();

    @Data
    public static class SipProperties {
        /**
         * 是否启用 SIP 服务
         */
        private Boolean enabled = true;
        /**
         * SIP 服务监听 IP
         */
        private String ip = "0.0.0.0";
        /**
         * SIP 暴露给外网/设备的映射 IP (若为空则自动取 ip)
         */
        private String showIp;
        /**
         * SIP 端口
         */
        private Integer port = 5061;
        /**
         * 国标域编码 (20位)
         */
        private String domain = "3402000000";
        /**
         * SIP 服务国标 ID (20位)
         */
        private String id = "34020000002000000001";
        /**
         * 默认接入认证密码
         */
        private String password = "admin";
        /**
         * 是否接收报警
         */
        private Boolean alarm = true;
        /**
         * 默认云台转动速度 (1-255)
         */
        private Integer ptzSpeed = 50;
        /**
         * 监控多网卡 IP 列表
         */
        private List<String> monitorIps;
    }

    @Data
    public static class MediaProperties {
        /**
         * 默认主媒体服务器 ID
         */
        private String defaultServerId = "default";
        /**
         * 媒体服务器 IP
         */
        private String ip = "127.0.0.1";
        /**
         * 媒体服务器 HTTP API 端口
         */
        private Integer httpPort = 80;
        /**
         * ZLM API Secret 秘钥
         */
        private String secret = "035c73f7-bb6b-4889-a715-d9eb2d1925cc";
        /**
         * ZLM 调用应用服务 Webhook 的回调可达 IP / 域名
         */
        private String hookIp = "127.0.0.1";
        /**
         * 无人观看时是否自动断流/释放端口
         */
        private Boolean autoCloseIdleStream = true;
    }

    @Data
    public static class RecordProperties {
        /**
         * 录像挂载目录
         */
        private String mountPath = "/data/record/";
        /**
         * 默认保留天数
         */
        private Integer retentionDays = 30;
    }
}
