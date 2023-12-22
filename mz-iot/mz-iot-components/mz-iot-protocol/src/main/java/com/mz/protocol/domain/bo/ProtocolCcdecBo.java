package com.mz.protocol.domain.bo;

import com.mz.protocol.domain.ProtocolCodec;
import com.mz.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import com.mz.common.core.validate.AddGroup;
import com.mz.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 协议编解码器业务对象 protocol_ccdec
 *
 * @author MzMedia
 * @date 2023-12-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ProtocolCodec.class, reverseConvertGenerate = false)
public class ProtocolCcdecBo extends BaseEntity {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 协议编解码名称
     */
    @NotBlank(message = "协议编解码名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 协议编解码描述
     */
    @NotBlank(message = "协议编解码描述不能为空", groups = { AddGroup.class, EditGroup.class })
    private String describe;

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
