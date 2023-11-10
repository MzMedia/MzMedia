package com.mz.iot.domain;

import com.mz.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 设备用户对象 device_user
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("device_user")
public class DeviceUser extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "user_id")
    private Long userId;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 设备ID
     */
    //@TableId(value = "device_id",type = IdType.INPUT)
    private Long deviceId;

    /**
     * 手机号码
     */
    private String phonenumber;

    /**
     * 是否为设备所有者（0=否，1=是）
     */
    private Long isOwner;

    /**
     * 用户物模型权限，多个以英文逗号分隔
     */
    private String perms;

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
