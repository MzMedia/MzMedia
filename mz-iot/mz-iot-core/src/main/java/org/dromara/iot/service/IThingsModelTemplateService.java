package org.dromara.iot.service;

import org.dromara.iot.domain.vo.ThingsModelTemplateVo;
import org.dromara.iot.domain.bo.ThingsModelTemplateBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 物模型模板Service接口
 *
 * @author MzMedia
 * @date 2025-01-14
 */
public interface IThingsModelTemplateService {

    /**
     * 查询物模型模板
     *
     * @param id 主键
     * @return 物模型模板
     */
    ThingsModelTemplateVo queryById(Long id);

    /**
     * 分页查询物模型模板列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 物模型模板分页列表
     */
    TableDataInfo<ThingsModelTemplateVo> queryPageList(ThingsModelTemplateBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的物模型模板列表
     *
     * @param bo 查询条件
     * @return 物模型模板列表
     */
    List<ThingsModelTemplateVo> queryList(ThingsModelTemplateBo bo);

    /**
     * 新增物模型模板
     *
     * @param bo 物模型模板
     * @return 是否新增成功
     */
    Boolean insertByBo(ThingsModelTemplateBo bo);

    /**
     * 修改物模型模板
     *
     * @param bo 物模型模板
     * @return 是否修改成功
     */
    Boolean updateByBo(ThingsModelTemplateBo bo);

    /**
     * 校验并批量删除物模型模板信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
