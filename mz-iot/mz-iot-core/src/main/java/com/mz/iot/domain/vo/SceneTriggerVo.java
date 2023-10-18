package com.mz.iot.domain.vo;

import com.mz.iot.domain.SceneTrigger;
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
 * 场景联动触发器视图对象 scene_trigger
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SceneTrigger.class)
public class SceneTriggerVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 场景触发器ID
     */
    @ExcelProperty(value = "场景触发器ID")
    private Long sceneTriggerId;

    /**
     * 所属场景ID
     */
    @ExcelProperty(value = "所属场景ID")
    private Long sceneId;

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
     * 告警状态（1-启动，2-停止）
     */
    @ExcelProperty(value = "告警状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1=-启动，2-停止")
    private Integer status;

    /**
     * 触发源（1=设备触发，2=定时触发）
     */
    @ExcelProperty(value = "触发源", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1==设备触发，2=定时触发")
    private Integer source;

    /**
     * 物模型标识符
     */
    @ExcelProperty(value = "物模型标识符")
    private String id;

    /**
     * 物模型名称
     */
    @ExcelProperty(value = "物模型名称")
    private String name;

    /**
     * 物模型值
     */
    @ExcelProperty(value = "物模型值")
    private String value;

    /**
     * 操作符
     */
    @ExcelProperty(value = "操作符")
    private String operator;

    /**
     * 物模型类别（1=属性，2=功能，3=事件，4=设备升级，5=设备上线，6=设备下线）
     */
    @ExcelProperty(value = "物模型类别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1==属性，2=功能，3=事件，4=设备升级，5=设备上线，6=设备下线")
    private Integer type;


}
