package com.mz.protocol.domain.vo;

import com.mz.protocol.domain.Protocol;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * 协议视图对象 protocol
 *
 * @author MzMedia
 * @date 2023-12-25
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Protocol.class)
public class ProtocolVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ExcelProperty(value = "ID")
    private Long id;

    /**
     * 协议名称
     */
    @ExcelProperty(value = "协议名称")
    private String name;

    /**
     * jar包文件
     */
    @ExcelProperty(value = "jar包文件")
    private String jarFile;

    /**
     * 编码脚本文件
     */
    @ExcelProperty(value = "编码脚本文件")
    private String codecFile;

    /**
     * 协议网络
     */
    @ExcelProperty(value = "协议网络")
    private String network;

    /**
     * 协议配置
     */
    @ExcelProperty(value = "协议配置")
    private String config;

    /**
     * 编解码器ID
     */
    @ExcelProperty(value = "编解码器ID")
    private Long codecId;

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
     * 协议运行状态
     */
    @ExcelProperty(value = "协议运行状态")
    private String state;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
