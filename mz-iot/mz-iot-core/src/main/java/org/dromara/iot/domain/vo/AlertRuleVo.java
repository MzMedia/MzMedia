package org.dromara.iot.domain.vo;

import org.dromara.iot.domain.AlertRule;
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
 * 设备告警规则视图对象 alert_rule
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = AlertRule.class)
public class AlertRuleVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 告警ID
     */
    @ExcelProperty(value = "告警ID")
    private Long id;

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
     * 所属场景ID
     */
    @ExcelProperty(value = "所属场景ID")
    private Long sceneId;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
