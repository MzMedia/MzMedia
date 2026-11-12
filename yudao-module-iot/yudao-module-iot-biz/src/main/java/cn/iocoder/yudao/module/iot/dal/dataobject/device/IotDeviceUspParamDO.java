package cn.iocoder.yudao.module.iot.dal.dataobject.device;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * IoT 设备 USP (TR-369) 参数快照 DO
 *
 * @author 芋道源码
 */
@TableName("iot_device_usp_param")
@KeySequence("iot_device_usp_param_seq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IotDeviceUspParamDO extends TenantBaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 设备编号
     */
    private Long deviceId;

    /**
     * USP 参数/对象路径，如 Device.WiFi.SSID.1.SSID
     */
    private String path;

    /**
     * 参数值
     */
    private String value;

    /**
     * 数据类型，如 string, boolean, unsignedInt
     */
    private String type;

    /**
     * 是否可写 (0-只读 1-可写)
     */
    private Boolean writable;

}
