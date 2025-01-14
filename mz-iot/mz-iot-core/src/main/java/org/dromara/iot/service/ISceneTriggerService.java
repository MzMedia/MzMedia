package org.dromara.iot.service;

import org.dromara.iot.domain.vo.SceneTriggerVo;
import org.dromara.iot.domain.bo.SceneTriggerBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 场景联动触发器Service接口
 *
 * @author MzMedia
 * @date 2025-01-14
 */
public interface ISceneTriggerService {

    /**
     * 查询场景联动触发器
     *
     * @param id 主键
     * @return 场景联动触发器
     */
    SceneTriggerVo queryById(Long id);

    /**
     * 分页查询场景联动触发器列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 场景联动触发器分页列表
     */
    TableDataInfo<SceneTriggerVo> queryPageList(SceneTriggerBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的场景联动触发器列表
     *
     * @param bo 查询条件
     * @return 场景联动触发器列表
     */
    List<SceneTriggerVo> queryList(SceneTriggerBo bo);

    /**
     * 新增场景联动触发器
     *
     * @param bo 场景联动触发器
     * @return 是否新增成功
     */
    Boolean insertByBo(SceneTriggerBo bo);

    /**
     * 修改场景联动触发器
     *
     * @param bo 场景联动触发器
     * @return 是否修改成功
     */
    Boolean updateByBo(SceneTriggerBo bo);

    /**
     * 校验并批量删除场景联动触发器信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
