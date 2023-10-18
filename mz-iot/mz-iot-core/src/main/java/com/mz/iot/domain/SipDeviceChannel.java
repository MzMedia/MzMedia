package com.mz.iot.domain;

import com.mz.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 监控设备通道信息对象 sip_device_channel
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sip_device_channel")
public class SipDeviceChannel extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 设备SipID
     */
    @TableId(value = "device_sip_id")
    private String deviceSipId;

    /**
     * 通道SipID
     */
    private String channelSipId;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 注册时间
     */
    private Date registerTime;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 通道类型
     */
    private String channelType;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 行政区域
     */
    private String civilCode;

    /**
     * 厂商名称
     */
    private String manufacture;

    /**
     * 产品型号
     */
    private String model;

    /**
     * 设备归属
     */
    private String owner;

    /**
     * 警区
     */
    private String block;

    /**
     * 安装地址
     */
    private String address;

    /**
     * 父级id
     */
    private String parentId;

    /**
     * 设备入网IP
     */
    private String ipAddress;

    /**
     * 设备接入端口号
     */
    private Long port;

    /**
     * 密码
     */
    private String password;

    /**
     * PTZ类型
     */
    private Long PTZType;

    /**
     * PTZ类型描述字符串
     */
    private String PTZTypeText;

    /**
     * 设备状态（1-未激活，2-禁用，3-在线，4-离线）
     */
    private Integer status;

    /**
     * 设备经度
     */
    private BigDecimal longitude;

    /**
     * 设备纬度
     */
    private BigDecimal latitude;

    /**
     * 流媒体ID
     */
    private String streamId;

    /**
     * 子设备数
     */
    private Long subCount;

    /**
     * 是否有子设备（1-有, 0-没有）
     */
    private Integer parental;

    /**
     * 是否含有音频（1-有, 0-没有）
     */
    private Integer hasAudio;

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
