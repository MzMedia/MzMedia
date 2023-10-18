package com.mz.iot.domain;

import com.mz.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 产品分类对象 category
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("category")
public class Category extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 产品分类ID
     */
    @TableId(value = "category_id")
    private Long categoryId;

    /**
     * 产品分类名称
     */
    private String categoryName;

    /**
     * 是否系统通用（0-否，1-是）
     */
    private Integer isSys;

    /**
     * 父级ID
     */
    private Long parentId;

    /**
     * 显示顺序
     */
    private Long orderNum;

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
