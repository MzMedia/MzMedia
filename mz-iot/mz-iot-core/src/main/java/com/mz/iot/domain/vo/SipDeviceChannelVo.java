package com.mz.iot.domain.vo;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mz.iot.domain.SipDeviceChannel;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.mz.common.excel.annotation.ExcelDictFormat;
import com.mz.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 监控设备通道信息视图对象 sip_device_channel
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SipDeviceChannel.class)
public class SipDeviceChannelVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 产品ID
     */
    @ExcelProperty(value = "产品ID")
    private Long productId;

    /**
     * 产品名称
     */
    @ExcelProperty(value = "产品名称")
    private String productName;

    /**
     * 设备SipID
     */
    @ExcelProperty(value = "设备SipID")
    private String deviceSipId;

    /**
     * 通道SipID
     */
    @ExcelProperty(value = "通道SipID")
    private String channelSipId;

    /**
     * 通道名称
     */
    @ExcelProperty(value = "通道名称")
    private String channelName;

    /**
     * 注册时间
     */
    @ExcelProperty(value = "注册时间")
    private Date registerTime;

    /**
     * 设备类型
     */
    @ExcelProperty(value = "设备类型")
    private String deviceType;

    /**
     * 通道类型
     */
    @ExcelProperty(value = "通道类型")
    private String channelType;

    /**
     * 城市编码
     */
    @ExcelProperty(value = "城市编码")
    private String cityCode;

    /**
     * 行政区域
     */
    @ExcelProperty(value = "行政区域")
    private String civilCode;

    /**
     * 厂商名称
     */
    @ExcelProperty(value = "厂商名称")
    private String manufacture;

    /**
     * 产品型号
     */
    @ExcelProperty(value = "产品型号")
    private String model;

    /**
     * 设备归属
     */
    @ExcelProperty(value = "设备归属")
    private String owner;

    /**
     * 警区
     */
    @ExcelProperty(value = "警区")
    private String block;

    /**
     * 安装地址
     */
    @ExcelProperty(value = "安装地址")
    private String address;

    /**
     * 父级id
     */
    @ExcelProperty(value = "父级id")
    private String parentId;

    /**
     * 设备入网IP
     */
    @ExcelProperty(value = "设备入网IP")
    private String ipAddress;

    /**
     * 设备接入端口号
     */
    @ExcelProperty(value = "设备接入端口号")
    private Long port;

    /**
     * 密码
     */
    @ExcelProperty(value = "密码")
    private String password;

    /**
     * PTZ类型
     */
    @ExcelProperty(value = "PTZ类型")
    private Long PTZType;

    /**
     * PTZ类型描述字符串
     */
    @ExcelProperty(value = "PTZ类型描述字符串")
    private String PTZTypeText;

    /**
     * 设备状态（1-未激活，2-禁用，3-在线，4-离线）
     */
    @ExcelProperty(value = "设备状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1=-未激活，2-禁用，3-在线，4-离线")
    private Integer status;

    /**
     * 设备经度
     */
    @ExcelProperty(value = "设备经度")
    private BigDecimal longitude;

    /**
     * 设备纬度
     */
    @ExcelProperty(value = "设备纬度")
    private BigDecimal latitude;

    /**
     * 流媒体ID
     */
    @ExcelProperty(value = "流媒体ID")
    private String streamId;

    /**
     * 子设备数
     */
    @ExcelProperty(value = "子设备数")
    private Long subCount;

    /**
     * 是否有子设备（1-有, 0-没有）
     */
    @ExcelProperty(value = "是否有子设备", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1=-有,,0=-没有")
    private Integer parental;

    /**
     * 是否含有音频（1-有, 0-没有）
     */
    @ExcelProperty(value = "是否含有音频", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1=-有,,0=-没有")
    private Integer hasAudio;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
