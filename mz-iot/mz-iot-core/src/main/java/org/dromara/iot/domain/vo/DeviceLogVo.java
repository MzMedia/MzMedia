package org.dromara.iot.domain.vo;

import org.dromara.iot.domain.DeviceLog;
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
 * 设备日志视图对象 device_log
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = DeviceLog.class)
public class DeviceLogVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 设备监测信息ID
     */
    @ExcelProperty(value = "设备监测信息ID")
    private Long id;

    /**
     * 设备ID
     */
    @ExcelProperty(value = "设备ID")
    private Long deviceId;

    /**
     * 标识符
     */
    @ExcelProperty(value = "标识符")
    private String identity;

    /**
     * 物模型名称
     */
    @ExcelProperty(value = "物模型名称")
    private String modelName;

    /**
     * 类型（1=属性上报，2=调用功能，3=事件上报，4=设备升级，5=设备上线，6=设备离线）
     */
    @ExcelProperty(value = "类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1==属性上报，2=调用功能，3=事件上报，4=设备升级，5=设备上线，6=设备离线")
    private Long logType;

    /**
     * 日志值
     */
    @ExcelProperty(value = "日志值")
    private String logValue;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
