package com.mz.iot.domain.bo;

import com.mz.iot.domain.Product;
import com.mz.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import com.mz.common.core.validate.AddGroup;
import com.mz.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 产品业务对象 product
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Product.class, reverseConvertGenerate = false)
public class ProductBo extends BaseEntity {

    /**
     * 产品分类ID
     */
    @NotNull(message = "产品分类ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long categoryId;

    /**
     * 产品ID
     */
    @NotNull(message = "产品ID不能为空", groups = { EditGroup.class })
    private Long productId;

    /**
     * 产品名称
     */
    @NotBlank(message = "产品名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String productName;

    /**
     * 协议编号
     */
    @NotBlank(message = "协议编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String protocolCode;

    /**
     * 是否系统通用（0-否，1-是）
     */
    @NotNull(message = "是否系统通用（0-否，1-是）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer isSys;

    /**
     * 是否启用授权码（0-否，1-是）
     */
    @NotNull(message = "是否启用授权码（0-否，1-是）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer isAuthorize;

    /**
     * mqtt账号
     */
    @NotBlank(message = "mqtt账号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String mqttAccount;

    /**
     * mqtt密码
     */
    @NotBlank(message = "mqtt密码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String mqttPassword;

    /**
     * 产品秘钥
     */
    @NotBlank(message = "产品秘钥不能为空", groups = { AddGroup.class, EditGroup.class })
    private String mqttSecret;

    /**
     * 状态（1-未发布，2-已发布）
     */
    @NotNull(message = "状态（1-未发布，2-已发布）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer status;

    /**
     * 物模型JSON（属性、功能、事件）
     */
    @NotBlank(message = "物模型JSON（属性、功能、事件）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String thingsModelsJson;

    /**
     * 设备类型（1-直连设备、2-网关设备、3-监控设备）
     */
    @NotNull(message = "设备类型（1-直连设备、2-网关设备、3-监控设备）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer deviceType;

    /**
     * 联网方式（1=wifi、2=蜂窝(2G/3G/4G/5G)、3=以太网、4=其他）
     */
    @NotNull(message = "联网方式（1=wifi、2=蜂窝(2G/3G/4G/5G)、3=以太网、4=其他）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer networkMethod;

    /**
     * 认证方式（1-简单认证、2-加密认证、3-简单+加密）
     */
    @NotNull(message = "认证方式（1-简单认证、2-加密认证、3-简单+加密）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer vertificateMethod;

    /**
     * 图片地址
     */
    @NotBlank(message = "图片地址不能为空", groups = { AddGroup.class, EditGroup.class })
    private String imgUrl;

    /**
     * 备注
     */
    @NotBlank(message = "备注不能为空", groups = { AddGroup.class, EditGroup.class })
    private String remark;


}
