package com.mz.iot.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mz.iot.domain.Device;
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
 * 设备信息视图对象 device
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Device.class)
public class DeviceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户ID")
    private Long userId;

    /**
     * 子用户ID
     */
    @ExcelProperty(value = "子用户ID")
    private Long subUid;

    /**
     * 设备ID
     */
    @ExcelProperty(value = "设备ID")
    private Long deviceId;

    /**
     * 子设备ID
     */
    @ExcelProperty(value = "子设备ID")
    private Long subDid;

    /**
     * 设备名称
     */
    @ExcelProperty(value = "设备名称")
    private String deviceName;

    /**
     * 设备昵称
     */
    @ExcelProperty(value = "设备昵称")
    private String nickName;

    /**
     * 产品key
     */
    @ExcelProperty(value = "产品key")
    private String productKey;

    /**
     * 设备编号
     */
    @ExcelProperty(value = "设备编号")
    private String serialNumber;

    /**
     * 产品密钥
     */
    @ExcelProperty(value = "产品密钥")
    private String secret;

    /**
     * 固件版本
     */
    @ExcelProperty(value = "固件版本")
    private String firmwareVersion;

    /**
     * 设备昵称
     */
    @ExcelProperty(value = "设备昵称")
    private String locate;

    /**
     * 设备昵称
     */
    @ExcelProperty(value = "设备昵称")
    private String tag;

    /**
     * 设备状态（1-未激活，2-禁用，3-在线，4-离线）
     */
    @ExcelProperty(value = "设备状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1=-未激活，2-禁用，3-在线，4-离线")
    private Integer status;

    /**
     * 设备IP
     */
    @ExcelProperty(value = "设备IP")
    private String ip;

    /**
     * 激活时间
     */
    @ExcelProperty(value = "激活时间")
    private Date activeTime;

    /**
     * 物模型值
     */
    @ExcelProperty(value = "物模型值")
    private String thingsModel;

    /**
     * 设备摘要，格式[{"name":"device"},{"chip":"esp8266"}]
     */
    @ExcelProperty(value = "设备摘要")
    private String summary;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
