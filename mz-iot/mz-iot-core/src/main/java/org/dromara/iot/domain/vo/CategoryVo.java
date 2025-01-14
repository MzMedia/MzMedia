package org.dromara.iot.domain.vo;

import org.dromara.iot.domain.Category;
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
 * 产品分类视图对象 category
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Category.class)
public class CategoryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 产品分类ID
     */
    @ExcelProperty(value = "产品分类ID")
    private Long id;

    /**
     * 产品分类名称
     */
    @ExcelProperty(value = "产品分类名称")
    private String categoryName;

    /**
     * 是否系统通用（0-否，1-是）
     */
    @ExcelProperty(value = "是否系统通用", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=-否，1-是")
    private Long isSys;

    /**
     * 父级ID
     */
    @ExcelProperty(value = "父级ID")
    private Long parentId;

    /**
     * 显示顺序
     */
    @ExcelProperty(value = "显示顺序")
    private Long orderNum;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
