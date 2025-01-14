package org.dromara.iot.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 物模型对象 things_model
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("things_model")
public class ThingsModel extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 物模型ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 物模型名称
     */
    private String modelName;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 标识符，产品下唯一
     */
    private String identifier;

    /**
     * 模型类别（1-属性，2-功能，3-事件）
     */
    private Long type;

    /**
     * 数据类型（integer、decimal、string、bool、array、enum）
     */
    private String datatype;

    /**
     * 数据定义
     */
    private String specs;

    /**
     * 是否图表展示（0-否，1-是）
     */
    private Long isChart;

    /**
     * 是否实时监测（0-否，1-是）
     */
    private Long isMonitor;

    /**
     * 是否历史存储（0-否，1-是）
     */
    private Long isHistory;

    /**
     * 是否只读数据(0-否，1-是)
     */
    private Long isReadonly;

    /**
     * 是否设备分享权限(0-否，1-是)
     */
    private Long isSharePerm;

    /**
     * 排序，值越大，排序越靠前
     */
    private Long modelOrder;

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
