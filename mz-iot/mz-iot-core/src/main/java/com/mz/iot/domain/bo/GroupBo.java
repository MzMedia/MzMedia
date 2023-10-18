package com.mz.iot.domain.bo;

import com.mz.iot.domain.Group;
import com.mz.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import com.mz.common.core.validate.AddGroup;
import com.mz.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 设备分组业务对象 group
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Group.class, reverseConvertGenerate = false)
public class GroupBo extends BaseEntity {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 分组ID
     */
    @NotNull(message = "分组ID不能为空", groups = { EditGroup.class })
    private Long groupId;

    /**
     * 分组名称
     */
    @NotBlank(message = "分组名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String groupName;

    /**
     * 分组排序
     */
    @NotNull(message = "分组排序不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long groupOrder;

    /**
     * 备注
     */
    @NotBlank(message = "备注不能为空", groups = { AddGroup.class, EditGroup.class })
    private String remark;


}
