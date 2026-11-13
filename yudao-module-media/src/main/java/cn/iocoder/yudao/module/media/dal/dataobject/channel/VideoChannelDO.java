package cn.iocoder.yudao.module.media.dal.dataobject.channel;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 视频监控 - 设备通道实体类
 *
 * @author MzMedia Architect
 */
@TableName("mz_video_channel")
@Data
@EqualsAndHashCode(callSuper = true)
public class VideoChannelDO extends TenantBaseDO {

    /**
     * 自增主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 通道国标编码 (20位)
     */
    private String channelId;

    /**
     * 所属主设备国标编号
     */
    private String deviceId;

    /**
     * 通道名称
     */
    private String name;

    /**
     * 自定义通道名称
     */
    private String customName;

    /**
     * 厂商
     */
    private String manufacturer;

    /**
     * 型号
     */
    private String model;

    /**
     * 安装地址
     */
    private String address;

    /**
     * 行政区划代码
     */
    private String civilCode;

    /**
     * 通道状态 (0:离线, 1:在线, 2:推流中)
     */
    private Integer status;

    /**
     * 云台类型 (0:无, 1:球机, 2:半球, 3:固定枪机, 4:遥控枪机)
     */
    private Integer ptzType;

    /**
     * 当前点播分配的流 ID
     */
    private String streamId;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 数据源类型 (1:GB28181, 2:推流, 3:代理, 4:ONVIF, 5:海康, 6:大华, 7:萤石, 200:JT1078)
     */
    private Integer dataType;

    /**
     * 关联外键数据源设备 ID
     */
    private Integer dataDeviceId;

    /**
     * 是否包含音频
     */
    private Integer hasAudio;
}
