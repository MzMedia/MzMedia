package com.mz.iot.domain.bo;

import com.mz.iot.domain.SipDevice;
import com.mz.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import com.mz.common.core.validate.AddGroup;
import com.mz.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 监控设备业务对象 sip_device
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SipDevice.class, reverseConvertGenerate = false)
public class SipDeviceBo extends BaseEntity {

    /**
     * 设备ID
     */
    @NotNull(message = "设备ID不能为空", groups = { EditGroup.class })
    private Long deviceId;

    /**
     * 产品ID
     */
    @NotNull(message = "产品ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long productId;

    /**
     * 设备SipID
     */
    @NotBlank(message = "设备SipID不能为空", groups = { AddGroup.class, EditGroup.class })
    private String deviceSipId;

    /**
     * 设备名称
     */
    @NotBlank(message = "设备名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String deviceName;

    /**
     * 厂商名称
     */
    @NotBlank(message = "厂商名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String manufacturer;

    /**
     * 产品型号
     */
    @NotBlank(message = "产品型号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String model;

    /**
     * 固件版本
     */
    @NotBlank(message = "固件版本不能为空", groups = { AddGroup.class, EditGroup.class })
    private String firmware;

    /**
     * 传输模式
     */
    @NotBlank(message = "传输模式不能为空", groups = { AddGroup.class, EditGroup.class })
    private String transport;

    /**
     * 流模式
     */
    @NotBlank(message = "流模式不能为空", groups = { AddGroup.class, EditGroup.class })
    private String streamMode;

    /**
     * 在线状态
     */
    @NotBlank(message = "在线状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private String online;

    /**
     * 注册时间
     */
    @NotNull(message = "注册时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date registerTime;

    /**
     * 最后上线时间
     */
    @NotNull(message = "最后上线时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date lastConnectTime;

    /**
     * 激活时间
     */
    @NotNull(message = "激活时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date activeTime;

    /**
     * 设备入网IP
     */
    @NotBlank(message = "设备入网IP不能为空", groups = { AddGroup.class, EditGroup.class })
    private String ip;

    /**
     * 设备接入端口号
     */
    @NotNull(message = "设备接入端口号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long port;

    /**
     * 设备地址
     */
    @NotBlank(message = "设备地址不能为空", groups = { AddGroup.class, EditGroup.class })
    private String hostAddress;

    /**
     * 备注
     */
    @NotBlank(message = "备注不能为空", groups = { AddGroup.class, EditGroup.class })
    private String remark;


}
