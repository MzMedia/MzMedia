package org.dromara.iot.domain.vo;

import org.dromara.iot.domain.AlertLog;
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
 * 设备告警日志视图对象 alert_log
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = AlertLog.class)
public class AlertLogVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 告警日志ID
     */
    @ExcelProperty(value = "告警日志ID")
    private Long id;

    /**
     * 设备ID
     */
    @ExcelProperty(value = "设备ID")
    private Long deviceId;

    /**
     * 产品ID
     */
    @ExcelProperty(value = "产品ID")
    private Long productId;

    /**
     * 告警名称
     */
    @ExcelProperty(value = "告警名称")
    private String alertName;

    /**
     * 告警级别（1=提醒通知，2=轻微问题，3=严重警告）
     */
    @ExcelProperty(value = "告警级别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1==提醒通知，2=轻微问题，3=严重警告")
    private Long alertLevel;

    /**
     * 告警详情（对应物模型）
     */
    @ExcelProperty(value = "告警详情", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "对=应物模型")
    private String detail;

    /**
     * 处理状态(1=不需要处理,2=未处理,3=已处理)
     */
    @ExcelProperty(value = "处理状态(1=不需要处理,2=未处理,3=已处理)")
    private Long status;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
