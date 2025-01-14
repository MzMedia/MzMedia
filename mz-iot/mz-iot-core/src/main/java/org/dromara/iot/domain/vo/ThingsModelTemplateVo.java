package org.dromara.iot.domain.vo;

import org.dromara.iot.domain.ThingsModelTemplate;
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
 * 物模型模板视图对象 things_model_template
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ThingsModelTemplate.class)
public class ThingsModelTemplateVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 物模型ID
     */
    @ExcelProperty(value = "物模型ID")
    private Long id;

    /**
     * 物模型名称
     */
    @ExcelProperty(value = "物模型名称")
    private String templateName;

    /**
     * 标识符，产品下唯一
     */
    @ExcelProperty(value = "标识符，产品下唯一")
    private String identifier;

    /**
     * 模型类别（1-属性，2-功能，3-事件）
     */
    @ExcelProperty(value = "模型类别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1=-属性，2-功能，3-事件")
    private Long type;

    /**
     * 数据类型（integer、decimal、string、bool、array、enum）
     */
    @ExcelProperty(value = "数据类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "i=nteger、decimal、string、bool、array、enum")
    private String datatype;

    /**
     * 数据定义
     */
    @ExcelProperty(value = "数据定义")
    private String specs;

    /**
     * 是否系统通用（0-否，1-是）
     */
    @ExcelProperty(value = "是否系统通用", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=-否，1-是")
    private Long isSys;

    /**
     * 是否图表展示（0-否，1-是）
     */
    @ExcelProperty(value = "是否图表展示", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=-否，1-是")
    private Long isChart;

    /**
     * 是否实时监测（0-否，1-是）
     */
    @ExcelProperty(value = "是否实时监测", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=-否，1-是")
    private Long isMonitor;

    /**
     * 是否历史存储 (0-否，1-是）
     */
    @ExcelProperty(value = "是否历史存储 (0-否，1-是）")
    private Long isHistory;

    /**
     * 是否只读数据(0-否，1-是)
     */
    @ExcelProperty(value = "是否只读数据(0-否，1-是)")
    private Long isReadonly;

    /**
     * 是否设备分享权限(0-否，1-是)
     */
    @ExcelProperty(value = "是否设备分享权限(0-否，1-是)")
    private Long isSharePerm;

    /**
     * 排序，值越大，排序越靠前
     */
    @ExcelProperty(value = "排序，值越大，排序越靠前")
    private Long modelOrder;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
