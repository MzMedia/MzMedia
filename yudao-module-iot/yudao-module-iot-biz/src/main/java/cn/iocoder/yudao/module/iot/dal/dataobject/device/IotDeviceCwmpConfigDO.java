package cn.iocoder.yudao.module.iot.dal.dataobject.device;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * IoT 设备 CWMP (TR-069) 配置 DO
 *
 * @author 芋道源码
 */
@TableName("iot_device_cwmp_config")
@KeySequence("iot_device_cwmp_config_seq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IotDeviceCwmpConfigDO extends TenantBaseDO {

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
     * 产品编号
     */
    private Long productId;

    /**
     * CPE 连接 ACS 的认证用户名
     */
    private String acsUsername;

    /**
     * CPE 连接 ACS 的认证密码
     */
    private String acsPassword;

    /**
     * CPE 接收连接请求的 URL (ConnectionRequestURL)
     */
    private String connectionRequestUrl;

    /**
     * ACS 发起连接请求的用户名
     */
    private String connectionRequestUsername;

    /**
     * ACS 发起连接请求的密码
     */
    private String connectionRequestPassword;

    /**
     * 是否开启周期上报
     */
    private Boolean periodicInformEnable;

    /**
     * 周期上报间隔（秒）
     */
    private Integer periodicInformInterval;

    /**
     * 最后一次 Inform 时间
     */
    private LocalDateTime lastInformTime;

    /**
     * 数据模型类型 (TR098 / TR181)
     */
    private String dataModelType;

    /**
     * 硬件版本
     */
    private String hardwareVersion;

    /**
     * 软件版本
     */
    private String softwareVersion;

    /**
     * 业务配置码
     */
    private String provisioningCode;

}
