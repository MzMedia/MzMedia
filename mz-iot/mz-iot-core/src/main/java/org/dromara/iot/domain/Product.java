package org.dromara.iot.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 产品对象 product
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product")
public class Product extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 产品ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 产品分类ID
     */
    private Long categoryId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 协议编号
     */
    private String protocolCode;

    /**
     * 是否系统通用（0-否，1-是）
     */
    private Long isSys;

    /**
     * 是否启用授权码（0-否，1-是）
     */
    private Long isAuthorize;

    /**
     * mqtt账号
     */
    private String mqttAccount;

    /**
     * mqtt密码
     */
    private String mqttPassword;

    /**
     * 产品秘钥
     */
    private String mqttSecret;

    /**
     * 状态（1-未发布，2-已发布）
     */
    private Long status;

    /**
     * 物模型JSON（属性、功能、事件）
     */
    private String thingsModelsJson;

    /**
     * 设备类型（1-直连设备、2-网关设备、3-监控设备）
     */
    private Long deviceType;

    /**
     * 联网方式（1=wifi、2=蜂窝(2G/3G/4G/5G)、3=以太网、4=其他）
     */
    private Long networkMethod;

    /**
     * 认证方式（1-简单认证、2-加密认证、3-简单+加密）
     */
    private Long vertificateMethod;

    /**
     * 图片地址
     */
    private String imgUrl;

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
