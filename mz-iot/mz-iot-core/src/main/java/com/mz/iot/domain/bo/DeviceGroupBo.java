package com.mz.iot.domain.bo;

import com.mz.iot.domain.DeviceGroup;
import com.mz.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import com.mz.common.core.validate.AddGroup;
import com.mz.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 设备分组业务对象 device_group
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = DeviceGroup.class, reverseConvertGenerate = false)
public class DeviceGroupBo extends BaseEntity {

    /**
     * 设备ID
     */
    @NotNull(message = "设备ID不能为空", groups = { EditGroup.class })
    private Long deviceId;

    /**
     * 分组ID
     */
    @NotNull(message = "分组ID不能为空", groups = { EditGroup.class })
    private Long groupId;


}
