package com.mz.iot.service;

import com.mz.iot.domain.SceneRule;
import com.mz.iot.domain.vo.SceneRuleVo;
import com.mz.iot.domain.bo.SceneRuleBo;
import com.mz.common.mybatis.core.page.TableDataInfo;
import com.mz.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 场景联动Service接口
 *
 * @author MzMedia
 * @date 2023-10-18
 */
public interface ISceneRuleService {

    /**
     * 查询场景联动
     */
    SceneRuleVo queryById(Long sceneId);

    /**
     * 查询场景联动列表
     */
    TableDataInfo<SceneRuleVo> queryPageList(SceneRuleBo bo, PageQuery pageQuery);

    /**
     * 查询场景联动列表
     */
    List<SceneRuleVo> queryList(SceneRuleBo bo);

    /**
     * 新增场景联动
     */
    Boolean insertByBo(SceneRuleBo bo);

    /**
     * 修改场景联动
     */
    Boolean updateByBo(SceneRuleBo bo);

    /**
     * 校验并批量删除场景联动信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
