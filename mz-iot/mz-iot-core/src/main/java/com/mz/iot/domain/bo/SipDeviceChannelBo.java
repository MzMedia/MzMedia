package com.mz.iot.domain.bo;

import com.mz.iot.domain.SipDeviceChannel;
import com.mz.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import com.mz.common.core.validate.AddGroup;
import com.mz.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 监控设备通道信息业务对象 sip_device_channel
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SipDeviceChannel.class, reverseConvertGenerate = false)
public class SipDeviceChannelBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 产品ID
     */
    @NotNull(message = "产品ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long productId;

    /**
     * 产品名称
     */
    @NotBlank(message = "产品名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String productName;

    /**
     * 设备SipID
     */
    @NotBlank(message = "设备SipID不能为空", groups = { EditGroup.class })
    private String deviceSipId;

    /**
     * 通道SipID
     */
    @NotBlank(message = "通道SipID不能为空", groups = { AddGroup.class, EditGroup.class })
    private String channelSipId;

    /**
     * 通道名称
     */
    @NotBlank(message = "通道名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String channelName;

    /**
     * 注册时间
     */
    @NotNull(message = "注册时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date registerTime;

    /**
     * 设备类型
     */
    @NotBlank(message = "设备类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String deviceType;

    /**
     * 通道类型
     */
    @NotBlank(message = "通道类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String channelType;

    /**
     * 城市编码
     */
    @NotBlank(message = "城市编码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String cityCode;

    /**
     * 行政区域
     */
    @NotBlank(message = "行政区域不能为空", groups = { AddGroup.class, EditGroup.class })
    private String civilCode;

    /**
     * 厂商名称
     */
    @NotBlank(message = "厂商名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String manufacture;

    /**
     * 产品型号
     */
    @NotBlank(message = "产品型号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String model;

    /**
     * 设备归属
     */
    @NotBlank(message = "设备归属不能为空", groups = { AddGroup.class, EditGroup.class })
    private String owner;

    /**
     * 警区
     */
    @NotBlank(message = "警区不能为空", groups = { AddGroup.class, EditGroup.class })
    private String block;

    /**
     * 安装地址
     */
    @NotBlank(message = "安装地址不能为空", groups = { AddGroup.class, EditGroup.class })
    private String address;

    /**
     * 父级id
     */
    @NotBlank(message = "父级id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String parentId;

    /**
     * 设备入网IP
     */
    @NotBlank(message = "设备入网IP不能为空", groups = { AddGroup.class, EditGroup.class })
    private String ipAddress;

    /**
     * 设备接入端口号
     */
    @NotNull(message = "设备接入端口号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long port;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String password;

    /**
     * PTZ类型
     */
    @NotNull(message = "PTZ类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long PTZType;

    /**
     * PTZ类型描述字符串
     */
    @NotBlank(message = "PTZ类型描述字符串不能为空", groups = { AddGroup.class, EditGroup.class })
    private String PTZTypeText;

    /**
     * 设备状态（1-未激活，2-禁用，3-在线，4-离线）
     */
    @NotNull(message = "设备状态（1-未激活，2-禁用，3-在线，4-离线）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer status;

    /**
     * 设备经度
     */
    @NotNull(message = "设备经度不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal longitude;

    /**
     * 设备纬度
     */
    @NotNull(message = "设备纬度不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal latitude;

    /**
     * 流媒体ID
     */
    @NotBlank(message = "流媒体ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private String streamId;

    /**
     * 子设备数
     */
    @NotNull(message = "子设备数不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long subCount;

    /**
     * 是否有子设备（1-有, 0-没有）
     */
    @NotNull(message = "是否有子设备（1-有, 0-没有）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer parental;

    /**
     * 是否含有音频（1-有, 0-没有）
     */
    @NotNull(message = "是否含有音频（1-有, 0-没有）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer hasAudio;

    /**
     * 备注
     */
    @NotBlank(message = "备注不能为空", groups = { AddGroup.class, EditGroup.class })
    private String remark;


}
