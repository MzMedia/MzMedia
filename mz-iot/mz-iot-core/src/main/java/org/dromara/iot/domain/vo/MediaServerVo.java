package org.dromara.iot.domain.vo;

import org.dromara.iot.domain.MediaServer;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 流媒体服务器配置视图对象 media_server
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = MediaServer.class)
public class MediaServerVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 流媒体配置ID
     */
    @ExcelProperty(value = "流媒体配置ID")
    private Long id;

    /**
     * 服务器标识
     */
    @ExcelProperty(value = "服务器标识")
    private String serverId;

    /**
     * 使能开关
     */
    @ExcelProperty(value = "使能开关")
    private Long enabled;

    /**
     * 默认播放协议
     */
    @ExcelProperty(value = "默认播放协议")
    private String protocol;

    /**
     * 服务器ip
     */
    @ExcelProperty(value = "服务器ip")
    private String ip;

    /**
     * 服务器域名
     */
    @ExcelProperty(value = "服务器域名")
    private String domain;

    /**
     * 回调服务器地址
     */
    @ExcelProperty(value = "回调服务器地址")
    private String hookurl;

    /**
     * 流媒体密钥
     */
    @ExcelProperty(value = "流媒体密钥")
    private String secret;

    /**
     * http端口
     */
    @ExcelProperty(value = "http端口")
    private Long portHttp;

    /**
     * https端口
     */
    @ExcelProperty(value = "https端口")
    private Long portHttps;

    /**
     * rtmp端口
     */
    @ExcelProperty(value = "rtmp端口")
    private Long portRtmp;

    /**
     * rtsp端口
     */
    @ExcelProperty(value = "rtsp端口")
    private Long portRtsp;

    /**
     * RTP收流端口
     */
    @ExcelProperty(value = "RTP收流端口")
    private Long rtpProxyPort;

    /**
     * ws端口
     */
    @ExcelProperty(value = "ws端口")
    private Long portWs;

    /**
     * 是否使用多端口模式
     */
    @ExcelProperty(value = "是否使用多端口模式")
    private Long rtpEnable;

    /**
     * rtp端口范围
     */
    @ExcelProperty(value = "rtp端口范围")
    private String rtpPortRange;

    /**
     * 录像服务端口
     */
    @ExcelProperty(value = "录像服务端口")
    private Long recordPort;

    /**
     * 是否自动同步配置ZLM
     */
    @ExcelProperty(value = "是否自动同步配置ZLM")
    private Long autoConfig;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态")
    private Long status;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
