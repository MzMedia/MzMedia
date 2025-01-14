package org.dromara.protocol.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 协议对象 protocol
 *
 * @author MzMedia
 * @date 2023-12-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("protocol")
public class Protocol extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String STATE_STOPPED = "stopped";
    public static final String STATE_RUNNING = "running";

    public static final String CODEC_TYPE_JSON = "JSON";
    public static final String CODEC_TYPE_XML = "XML";
    public static final String CODEC_TYPE_SOAP = "SOAP";
    public static final String CODEC_TYPE_PROTOBUF = "PROTOBUF";
    public static final String CODEC_TYPE_CUSTOM_JAR = "CUSTOM_JAR";
    public static final String CODEC_TYPE_CUSTOM_SCRIPT = "CUSTOM_SCRIPT";
    public static final String SCRIPT_FILE_NAME = "protocol.js";

    /**
     * ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 协议名称
     */
    private String name;

    /**
     * jar包文件
     */
    private String jarFile;

    /**
     * 编码脚本文件
     */
    private String codecFile;

    /**
     * 协议网络
     */
    private String network;

    /**
     * 协议配置
     */
    private String config;

    /**
     * 编解码器ID
     */
    private Long codecId;

    /**
     * 编解码类型
     */
    private String codecType;

    /**
     * 脚本内容
     */
    private String script;

    /**
     * 脚本语言
     */
    private String scriptLang;

    /**
     * 协议运行状态
     */
    private String state;

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
