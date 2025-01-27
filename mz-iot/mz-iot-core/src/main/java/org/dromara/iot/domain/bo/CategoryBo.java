package org.dromara.iot.domain.bo;

import org.dromara.iot.domain.Category;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 产品分类业务对象 category
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Category.class, reverseConvertGenerate = false)
public class CategoryBo extends BaseEntity {

    /**
     * 产品分类ID
     */
    @NotNull(message = "产品分类ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 产品分类名称
     */
    @NotBlank(message = "产品分类名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String categoryName;

    /**
     * 是否系统通用（0-否，1-是）
     */
    @NotNull(message = "是否系统通用（0-否，1-是）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long isSys;

    /**
     * 父级ID
     */
    @NotNull(message = "父级ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long parentId;

    /**
     * 显示顺序
     */
    @NotNull(message = "显示顺序不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long orderNum;

    /**
     * 备注
     */
    @NotBlank(message = "备注不能为空", groups = { AddGroup.class, EditGroup.class })
    private String remark;


}
