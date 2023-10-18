package com.mz.iot.domain;

import com.mz.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 监控设备对象 sip_device
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sip_device")
public class SipDevice extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    @TableId(value = "device_id")
    private Long deviceId;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 设备SipID
     */
    private String deviceSipId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 厂商名称
     */
    private String manufacturer;

    /**
     * 产品型号
     */
    private String model;

    /**
     * 固件版本
     */
    private String firmware;

    /**
     * 传输模式
     */
    private String transport;

    /**
     * 流模式
     */
    private String streamMode;

    /**
     * 在线状态
     */
    private String online;

    /**
     * 注册时间
     */
    private Date registerTime;

    /**
     * 最后上线时间
     */
    private Date lastConnectTime;

    /**
     * 激活时间
     */
    private Date activeTime;

    /**
     * 设备入网IP
     */
    private String ip;

    /**
     * 设备接入端口号
     */
    private Long port;

    /**
     * 设备地址
     */
    private String hostAddress;

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
