package com.mz.iot.domain.vo;

import com.mz.iot.domain.SceneRule;
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
 * 场景联动视图对象 scene_rule
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SceneRule.class)
public class SceneRuleVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 场景ID
     */
    @ExcelProperty(value = "场景ID")
    private Long sceneId;

    /**
     * 场景名称
     */
    @ExcelProperty(value = "场景名称")
    private String sceneName;

    /**
     * 执行动作
     */
    @ExcelProperty(value = "执行动作")
    private String actions;

    /**
     * 场景状态（1-启动，2-停止）
     */
    @ExcelProperty(value = "场景状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1=-启动，2-停止")
    private Integer status;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
