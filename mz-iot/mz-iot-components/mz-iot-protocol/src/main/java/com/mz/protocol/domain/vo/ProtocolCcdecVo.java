package com.mz.protocol.domain.vo;

import com.mz.protocol.domain.ProtocolCodec;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * 协议编解码器视图对象 protocol_ccdec
 *
 * @author MzMedia
 * @date 2023-12-22
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ProtocolCodec.class)
public class ProtocolCcdecVo implements Serializable {

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
