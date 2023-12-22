package com.mz.protocol.domain;

import com.mz.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 协议编解码器对象 protocol_ccdec
 *
 * @author MzMedia
 * @date 2023-12-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("protocol_ccdec")
public class ProtocolCodec extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;
    public static final String SCRIPT_FILE_NAME = "codec.js";
    /**
     * ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 协议编解码名称
     */
    private String name;

    /**
     * 协议编解码描述
     */
    private String describe;

    /**
     * 脚本内容
     */
    private String script;

    /**
     * 脚本语言
     */
    private String scriptLang;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;

    /**
     * 备注
     */
    private String remark;


}
