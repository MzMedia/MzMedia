package org.dromara.iot.domain.bo;

import org.dromara.iot.domain.Device;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 设备信息业务对象 device
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Device.class, reverseConvertGenerate = false)
public class DeviceBo extends BaseEntity {

    /**
     * 设备ID
     */
    @NotNull(message = "设备ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 子用户ID
     */
    @NotNull(message = "子用户ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long subUid;

    /**
     * 子设备ID
     */
    @NotNull(message = "子设备ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long subDid;

    /**
     * 设备名称
     */
    @NotBlank(message = "设备名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String deviceName;

    /**
     * 设备昵称
     */
    @NotBlank(message = "设备昵称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String nickName;

    /**
     * 产品key
     */
    @NotBlank(message = "产品key不能为空", groups = { AddGroup.class, EditGroup.class })
    private String productKey;

    /**
     * 设备编号
     */
    @NotBlank(message = "设备编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String serialNumber;

    /**
     * 产品密钥
     */
    @NotBlank(message = "产品密钥不能为空", groups = { AddGroup.class, EditGroup.class })
    private String secret;

    /**
     * 固件版本
     */
    @NotBlank(message = "固件版本不能为空", groups = { AddGroup.class, EditGroup.class })
    private String firmwareVersion;

    /**
     * 设备位置
     */
    @NotBlank(message = "设备位置不能为空", groups = { AddGroup.class, EditGroup.class })
    private String locate;

    /**
     * 设备标签
     */
    @NotBlank(message = "设备标签不能为空", groups = { AddGroup.class, EditGroup.class })
    private String tag;

    /**
     * 设备状态（1-未激活，2-禁用，3-在线，4-离线）
     */
    @NotNull(message = "设备状态（1-未激活，2-禁用，3-在线，4-离线）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long status;

    /**
     * 设备IP
     */
    @NotBlank(message = "设备IP不能为空", groups = { AddGroup.class, EditGroup.class })
    private String ip;

    /**
     * 激活时间
     */
    @NotNull(message = "激活时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date activeTime;

    /**
     * 物模型值
     */
    @NotBlank(message = "物模型值不能为空", groups = { AddGroup.class, EditGroup.class })
    private String thingsModel;

    /**
     * 设备摘要
     */
    @NotBlank(message = "设备摘要不能为空", groups = { AddGroup.class, EditGroup.class })
    private String summary;

    /**
     * 备注
     */
    @NotBlank(message = "备注不能为空", groups = { AddGroup.class, EditGroup.class })
    private String remark;


}
