package org.dromara.iot.service;

import org.dromara.iot.domain.vo.SceneRuleVo;
import org.dromara.iot.domain.bo.SceneRuleBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 场景联动Service接口
 *
 * @author MzMedia
 * @date 2025-01-14
 */
public interface ISceneRuleService {

    /**
     * 查询场景联动
     *
     * @param id 主键
     * @return 场景联动
     */
    SceneRuleVo queryById(Long id);

    /**
     * 分页查询场景联动列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 场景联动分页列表
     */
    TableDataInfo<SceneRuleVo> queryPageList(SceneRuleBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的场景联动列表
     *
     * @param bo 查询条件
     * @return 场景联动列表
     */
    List<SceneRuleVo> queryList(SceneRuleBo bo);

    /**
     * 新增场景联动
     *
     * @param bo 场景联动
     * @return 是否新增成功
     */
    Boolean insertByBo(SceneRuleBo bo);

    /**
     * 修改场景联动
     *
     * @param bo 场景联动
     * @return 是否修改成功
     */
    Boolean updateByBo(SceneRuleBo bo);

    /**
     * 校验并批量删除场景联动信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
