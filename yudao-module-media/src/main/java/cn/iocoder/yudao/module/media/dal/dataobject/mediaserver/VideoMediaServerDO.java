package cn.iocoder.yudao.module.media.dal.dataobject.mediaserver;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 视频监控 - 流媒体节点集群实体类 (ZLMediaKit)
 *
 * @author MzMedia Architect
 */
@TableName("mz_video_media_server")
@Data
@EqualsAndHashCode(callSuper = true)
public class VideoMediaServerDO extends BaseDO {

    /**
     * 媒体服务器唯一标识 ID
     */
    @TableId(type = IdType.INPUT)
    private String id;

    /**
     * 服务器内网 IP
     */
    private String ip;

    /**
     * ZLM 回调宿主 IP
     */
    private String hookIp;

    /**
     * SDP 协商 IP
     */
    private String sdpIp;

    /**
     * 推拉流外网 IP
     */
    private String streamIp;

    /**
     * HTTP 端口
     */
    private Integer httpPort;

    /**
     * HTTPS 端口
     */
    private Integer httpSslPort;

    /**
     * RTMP 端口
     */
    private Integer rtmpPort;

    /**
     * RTSP 端口
     */
    private Integer rtspPort;

    /**
     * REST API 秘钥
     */
    private String secret;

    /**
     * 是否启用 RTP 多端口
     */
    private Integer rtpEnable;

    /**
     * RTP 接收端口范围 (如 "30000,30500")
     */
    private String rtpPortRange;

    /**
     * RTP 发送端口范围 (如 "30501,31000")
     */
    private String sendRtpPortRange;

    /**
     * 是否为主默认服务器节点
     */
    private Integer defaultServer;

    /**
     * 节点在线状态 (0:离线, 1:在线)
     */
    private Integer status;

    /**
     * 最近保活心跳时间
     */
    private LocalDateTime lastKeepaliveTime;
}
