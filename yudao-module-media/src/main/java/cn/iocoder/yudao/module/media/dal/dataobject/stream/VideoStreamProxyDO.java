package cn.iocoder.yudao.module.media.dal.dataobject.stream;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 视频监控 - 拉流代理实体类 (RTSP/RTMP/FLV 转推)
 *
 * @author MzMedia Architect
 */
@TableName("mz_video_stream_proxy")
@Data
@EqualsAndHashCode(callSuper = true)
public class VideoStreamProxyDO extends TenantBaseDO {

    /**
     * 自增主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 应用名 (如 live)
     */
    private String app;

    /**
     * 流 ID
     */
    private String stream;

    /**
     * 流描述名称
     */
    private String name;

    /**
     * 目标拉流地址
     */
    private String url;

    /**
     * 源地址备份
     */
    private String srcUrl;

    /**
     * 超时时间 (毫秒)
     */
    private Integer timeoutMs;

    /**
     * 绑定的流媒体服务器 ID
     */
    private String mediaServerId;

    /**
     * 是否启用 (0:停用, 1:启用)
     */
    private Integer enable;

    /**
     * 是否启用音频
     */
    private Integer enableAudio;

    /**
     * 是否录制 MP4
     */
    private Integer enableMp4;

    /**
     * 无人观看时是否自动关闭
     */
    private Integer enableRemoveNoneReader;

    /**
     * 拉流状态 (0:未拉流, 1:拉流中)
     */
    private Integer status;
}
