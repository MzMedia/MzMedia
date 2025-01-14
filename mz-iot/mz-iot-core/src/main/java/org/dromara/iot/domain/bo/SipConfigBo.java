package org.dromara.iot.domain.bo;

import org.dromara.iot.domain.SipConfig;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * sip系统配置业务对象 sip_config
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SipConfig.class, reverseConvertGenerate = false)
public class SipConfigBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 产品ID
     */
    @NotNull(message = "产品ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long productId;

    /**
     * 使能开关
     */
    @NotNull(message = "使能开关不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long enabled;

    /**
     * 系统默认配置
     */
    @NotNull(message = "系统默认配置不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long isdefault;

    /**
     * 拓展sdp
     */
    @NotNull(message = "拓展sdp不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long seniorSdp;

    /**
     * 服务器域
     */
    @NotBlank(message = "服务器域不能为空", groups = { AddGroup.class, EditGroup.class })
    private String domain;

    /**
     * 服务器sipid
     */
    @NotBlank(message = "服务器sipid不能为空", groups = { AddGroup.class, EditGroup.class })
    private String sipid;

    /**
     * sip认证密码
     */
    @NotBlank(message = "sip认证密码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String password;

    /**
     * sip接入IP
     */
    @NotBlank(message = "sip接入IP不能为空", groups = { AddGroup.class, EditGroup.class })
    private String ip;

    /**
     * sip接入端口号
     */
    @NotNull(message = "sip接入端口号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long port;

    /**
     * 备注
     */
    @NotBlank(message = "备注不能为空", groups = { AddGroup.class, EditGroup.class })
    private String remark;


}
