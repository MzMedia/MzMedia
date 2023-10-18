package com.mz.iot.service;

import com.mz.iot.domain.SceneTrigger;
import com.mz.iot.domain.vo.SceneTriggerVo;
import com.mz.iot.domain.bo.SceneTriggerBo;
import com.mz.common.mybatis.core.page.TableDataInfo;
import com.mz.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 场景联动触发器Service接口
 *
 * @author MzMedia
 * @date 2023-10-18
 */
public interface ISceneTriggerService {

    /**
     * 查询场景联动触发器
     */
    SceneTriggerVo queryById(Long sceneTriggerId);

    /**
     * 查询场景联动触发器列表
     */
    TableDataInfo<SceneTriggerVo> queryPageList(SceneTriggerBo bo, PageQuery pageQuery);

    /**
     * 查询场景联动触发器列表
     */
    List<SceneTriggerVo> queryList(SceneTriggerBo bo);

    /**
     * 新增场景联动触发器
     */
    Boolean insertByBo(SceneTriggerBo bo);

    /**
     * 修改场景联动触发器
     */
    Boolean updateByBo(SceneTriggerBo bo);

    /**
     * 校验并批量删除场景联动触发器信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
