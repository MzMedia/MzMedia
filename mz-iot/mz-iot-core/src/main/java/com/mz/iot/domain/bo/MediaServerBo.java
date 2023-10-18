package com.mz.iot.domain.bo;

import com.mz.iot.domain.MediaServer;
import com.mz.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import com.mz.common.core.validate.AddGroup;
import com.mz.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 流媒体服务器配置业务对象 media_server
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MediaServer.class, reverseConvertGenerate = false)
public class MediaServerBo extends BaseEntity {

    /**
     * 流媒体配置ID
     */
    @NotNull(message = "流媒体配置ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 服务器标识
     */
    @NotBlank(message = "服务器标识不能为空", groups = { AddGroup.class, EditGroup.class })
    private String serverId;

    /**
     * 使能开关
     */
    @NotNull(message = "使能开关不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer enabled;

    /**
     * 默认播放协议
     */
    @NotBlank(message = "默认播放协议不能为空", groups = { AddGroup.class, EditGroup.class })
    private String protocol;

    /**
     * 服务器ip
     */
    @NotBlank(message = "服务器ip不能为空", groups = { AddGroup.class, EditGroup.class })
    private String ip;

    /**
     * 服务器域名
     */
    @NotBlank(message = "服务器域名不能为空", groups = { AddGroup.class, EditGroup.class })
    private String domain;

    /**
     * 回调服务器地址
     */
    @NotBlank(message = "回调服务器地址不能为空", groups = { AddGroup.class, EditGroup.class })
    private String hookurl;

    /**
     * 流媒体密钥
     */
    @NotBlank(message = "流媒体密钥不能为空", groups = { AddGroup.class, EditGroup.class })
    private String secret;

    /**
     * http端口
     */
    @NotNull(message = "http端口不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long portHttp;

    /**
     * https端口
     */
    @NotNull(message = "https端口不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long portHttps;

    /**
     * rtmp端口
     */
    @NotNull(message = "rtmp端口不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long portRtmp;

    /**
     * rtsp端口
     */
    @NotNull(message = "rtsp端口不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long portRtsp;

    /**
     * RTP收流端口
     */
    @NotNull(message = "RTP收流端口不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long rtpProxyPort;

    /**
     * ws端口
     */
    @NotNull(message = "ws端口不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long portWs;

    /**
     * 是否使用多端口模式
     */
    @NotNull(message = "是否使用多端口模式不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer rtpEnable;

    /**
     * rtp端口范围
     */
    @NotBlank(message = "rtp端口范围不能为空", groups = { AddGroup.class, EditGroup.class })
    private String rtpPortRange;

    /**
     * 录像服务端口
     */
    @NotNull(message = "录像服务端口不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long recordPort;

    /**
     * 是否自动同步配置ZLM
     */
    @NotNull(message = "是否自动同步配置ZLM不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer autoConfig;

    /**
     * 状态
     */
    @NotNull(message = "状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer status;

    /**
     * 备注
     */
    @NotBlank(message = "备注不能为空", groups = { AddGroup.class, EditGroup.class })
    private String remark;


}
