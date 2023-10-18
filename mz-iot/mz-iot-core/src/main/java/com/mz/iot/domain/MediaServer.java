package com.mz.iot.domain;

import com.mz.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 流媒体服务器配置对象 media_server
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("media_server")
public class MediaServer extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 流媒体配置ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 服务器标识
     */
    private String serverId;

    /**
     * 使能开关
     */
    private Integer enabled;

    /**
     * 默认播放协议
     */
    private String protocol;

    /**
     * 服务器ip
     */
    private String ip;

    /**
     * 服务器域名
     */
    private String domain;

    /**
     * 回调服务器地址
     */
    private String hookurl;

    /**
     * 流媒体密钥
     */
    private String secret;

    /**
     * http端口
     */
    private Long portHttp;

    /**
     * https端口
     */
    private Long portHttps;

    /**
     * rtmp端口
     */
    private Long portRtmp;

    /**
     * rtsp端口
     */
    private Long portRtsp;

    /**
     * RTP收流端口
     */
    private Long rtpProxyPort;

    /**
     * ws端口
     */
    private Long portWs;

    /**
     * 是否使用多端口模式
     */
    private Integer rtpEnable;

    /**
     * rtp端口范围
     */
    private String rtpPortRange;

    /**
     * 录像服务端口
     */
    private Long recordPort;

    /**
     * 是否自动同步配置ZLM
     */
    private Integer autoConfig;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;

    /**
     * 备注
     */
    private String remark;


}
