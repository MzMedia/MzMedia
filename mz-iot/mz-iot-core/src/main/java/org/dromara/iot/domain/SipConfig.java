package org.dromara.iot.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * sip系统配置对象 sip_config
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sip_config")
public class SipConfig extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 使能开关
     */
    private Long enabled;

    /**
     * 系统默认配置
     */
    private Long isdefault;

    /**
     * 拓展sdp
     */
    private Long seniorSdp;

    /**
     * 服务器域
     */
    private String domain;

    /**
     * 服务器sipid
     */
    private String sipid;

    /**
     * sip认证密码
     */
    private String password;

    /**
     * sip接入IP
     */
    private String ip;

    /**
     * sip接入端口号
     */
    private Long port;

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
