package com.mz.iot.domain.bo;

import com.mz.iot.domain.DeviceUser;
import com.mz.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import com.mz.common.core.validate.AddGroup;
import com.mz.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 设备用户业务对象 device_user
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = DeviceUser.class, reverseConvertGenerate = false)
public class DeviceUserBo extends BaseEntity {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空", groups = { EditGroup.class })
    private Long userId;

    /**
     * 用户昵称
     */
    @NotBlank(message = "用户昵称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String userName;

    /**
     * 设备ID
     */
    @NotNull(message = "设备ID不能为空", groups = { EditGroup.class })
    private Long deviceId;

    /**
     * 手机号码
     */
    @NotBlank(message = "手机号码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String phonenumber;

    /**
     * 是否为设备所有者（0=否，1=是）
     */
    @NotNull(message = "是否为设备所有者（0=否，1=是）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long isOwner;

    /**
     * 用户物模型权限，多个以英文逗号分隔
     */
    @NotBlank(message = "用户物模型权限，多个以英文逗号分隔不能为空", groups = { AddGroup.class, EditGroup.class })
    private String perms;

    /**
     * 备注
     */
    @NotBlank(message = "备注不能为空", groups = { AddGroup.class, EditGroup.class })
    private String remark;


}
