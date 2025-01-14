package org.dromara.iot.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.iot.domain.Device;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 设备信息视图对象 device
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Device.class)
public class DeviceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    @ExcelProperty(value = "设备ID")
    private Long id;

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
     * 设备位置
     */
    @ExcelProperty(value = "设备位置")
    private String locate;

    /**
     * 设备标签
     */
    @ExcelProperty(value = "设备标签")
    private String tag;

    /**
     * 设备状态（1-未激活，2-禁用，3-在线，4-离线）
     */
    @ExcelProperty(value = "设备状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1=-未激活，2-禁用，3-在线，4-离线")
    private Long status;

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
     * 设备摘要
     */
    @ExcelProperty(value = "设备摘要")
    private String summary;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
