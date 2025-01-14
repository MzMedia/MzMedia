package org.dromara.iot.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.iot.domain.SipDevice;
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
 * 监控设备视图对象 sip_device
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SipDevice.class)
public class SipDeviceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    @ExcelProperty(value = "设备ID")
    private Long id;

    /**
     * 产品ID
     */
    @ExcelProperty(value = "产品ID")
    private Long productId;

    /**
     * 设备SipID
     */
    @ExcelProperty(value = "设备SipID")
    private String deviceSipId;

    /**
     * 设备名称
     */
    @ExcelProperty(value = "设备名称")
    private String deviceName;

    /**
     * 厂商名称
     */
    @ExcelProperty(value = "厂商名称")
    private String manufacturer;

    /**
     * 产品型号
     */
    @ExcelProperty(value = "产品型号")
    private String model;

    /**
     * 固件版本
     */
    @ExcelProperty(value = "固件版本")
    private String firmware;

    /**
     * 传输模式
     */
    @ExcelProperty(value = "传输模式")
    private String transport;

    /**
     * 流模式
     */
    @ExcelProperty(value = "流模式")
    private String streamMode;

    /**
     * 在线状态
     */
    @ExcelProperty(value = "在线状态")
    private String online;

    /**
     * 注册时间
     */
    @ExcelProperty(value = "注册时间")
    private Date registerTime;

    /**
     * 最后上线时间
     */
    @ExcelProperty(value = "最后上线时间")
    private Date lastConnectTime;

    /**
     * 激活时间
     */
    @ExcelProperty(value = "激活时间")
    private Date activeTime;

    /**
     * 设备入网IP
     */
    @ExcelProperty(value = "设备入网IP")
    private String ip;

    /**
     * 设备接入端口号
     */
    @ExcelProperty(value = "设备接入端口号")
    private Long port;

    /**
     * 设备地址
     */
    @ExcelProperty(value = "设备地址")
    private String hostAddress;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
