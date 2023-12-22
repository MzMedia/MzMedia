package com.mz.protocol.domain.bo;

import com.mz.protocol.domain.Protocol;
import com.mz.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import com.mz.common.core.validate.AddGroup;
import com.mz.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 协议业务对象 protocol
 *
 * @author MzMedia
 * @date 2023-12-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Protocol.class, reverseConvertGenerate = false)
public class ProtocolBo extends BaseEntity {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 协议名称
     */
    @NotBlank(message = "协议名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * jar包文件
     */
    @NotBlank(message = "jar包文件不能为空", groups = { AddGroup.class, EditGroup.class })
    private String jarFile;

    /**
     * 编码脚本文件
     */
    @NotBlank(message = "编码脚本文件不能为空", groups = { AddGroup.class, EditGroup.class })
    private String codecFile;

    /**
     * 协议网络
     */
    @NotBlank(message = "协议网络不能为空", groups = { AddGroup.class, EditGroup.class })
    private String network;

    /**
     * 协议配置
     */
    @NotBlank(message = "协议配置不能为空", groups = { AddGroup.class, EditGroup.class })
    private String config;

    /**
     * 编解码器
     */
    @NotBlank(message = "编解码器不能为空", groups = { AddGroup.class, EditGroup.class })
    private String codec;

    /**
     * 编解码类型
     */
    @NotBlank(message = "编解码类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String codecType;

    /**
     * 脚本内容
     */
    @NotBlank(message = "脚本内容不能为空", groups = { AddGroup.class, EditGroup.class })
    private String script;

    /**
     * 脚本语言
     */
    @NotBlank(message = "脚本语言不能为空", groups = { AddGroup.class, EditGroup.class })
    private String scriptLang;

    /**
     * 备注
     */
    @NotBlank(message = "备注不能为空", groups = { AddGroup.class, EditGroup.class })
    private String remark;


}
