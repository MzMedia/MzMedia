package org.dromara.iot.domain.bo;

import org.dromara.iot.domain.ThingsModel;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 物模型业务对象 things_model
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ThingsModel.class, reverseConvertGenerate = false)
public class ThingsModelBo extends BaseEntity {

    /**
     * 物模型ID
     */
    @NotNull(message = "物模型ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 物模型名称
     */
    @NotBlank(message = "物模型名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String modelName;

    /**
     * 产品ID
     */
    @NotNull(message = "产品ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long productId;

    /**
     * 标识符，产品下唯一
     */
    @NotBlank(message = "标识符，产品下唯一不能为空", groups = { AddGroup.class, EditGroup.class })
    private String identifier;

    /**
     * 模型类别（1-属性，2-功能，3-事件）
     */
    @NotNull(message = "模型类别（1-属性，2-功能，3-事件）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long type;

    /**
     * 数据类型（integer、decimal、string、bool、array、enum）
     */
    @NotBlank(message = "数据类型（integer、decimal、string、bool、array、enum）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String datatype;

    /**
     * 数据定义
     */
    @NotBlank(message = "数据定义不能为空", groups = { AddGroup.class, EditGroup.class })
    private String specs;

    /**
     * 是否图表展示（0-否，1-是）
     */
    @NotNull(message = "是否图表展示（0-否，1-是）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long isChart;

    /**
     * 是否实时监测（0-否，1-是）
     */
    @NotNull(message = "是否实时监测（0-否，1-是）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long isMonitor;

    /**
     * 是否历史存储（0-否，1-是）
     */
    @NotNull(message = "是否历史存储（0-否，1-是）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long isHistory;

    /**
     * 是否只读数据(0-否，1-是)
     */
    @NotNull(message = "是否只读数据(0-否，1-是)不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long isReadonly;

    /**
     * 是否设备分享权限(0-否，1-是)
     */
    @NotNull(message = "是否设备分享权限(0-否，1-是)不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long isSharePerm;

    /**
     * 排序，值越大，排序越靠前
     */
    @NotNull(message = "排序，值越大，排序越靠前不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long modelOrder;

    /**
     * 备注
     */
    @NotBlank(message = "备注不能为空", groups = { AddGroup.class, EditGroup.class })
    private String remark;


}
