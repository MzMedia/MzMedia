package org.dromara.iot.domain.vo;

import org.dromara.iot.domain.ProtocolCodec;
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
 * 协议编解码器视图对象 protocol_codec
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ProtocolCodec.class)
public class ProtocolCodecVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ExcelProperty(value = "ID")
    private Long id;

    /**
     * 协议编解码名称
     */
    @ExcelProperty(value = "协议编解码名称")
    private String name;

    /**
     * 协议编解码描述
     */
    @ExcelProperty(value = "协议编解码描述")
    private String describe;

    /**
     * 编解码类型
     */
    @ExcelProperty(value = "编解码类型")
    private String codecType;

    /**
     * 脚本内容
     */
    @ExcelProperty(value = "脚本内容")
    private String script;

    /**
     * 脚本语言
     */
    @ExcelProperty(value = "脚本语言")
    private String scriptLang;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
