package org.dromara.iot.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.dromara.iot.domain.bo.SceneTriggerBo;
import org.dromara.iot.domain.vo.SceneTriggerVo;
import org.dromara.iot.domain.SceneTrigger;
import org.dromara.iot.mapper.SceneTriggerMapper;
import org.dromara.iot.service.ISceneTriggerService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 场景联动触发器Service业务层处理
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@RequiredArgsConstructor
@Service
public class SceneTriggerServiceImpl implements ISceneTriggerService {

    private final SceneTriggerMapper baseMapper;

    /**
     * 查询场景联动触发器
     *
     * @param id 主键
     * @return 场景联动触发器
     */
    @Override
    public SceneTriggerVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询场景联动触发器列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 场景联动触发器分页列表
     */
    @Override
    public TableDataInfo<SceneTriggerVo> queryPageList(SceneTriggerBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SceneTrigger> lqw = buildQueryWrapper(bo);
        Page<SceneTriggerVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的场景联动触发器列表
     *
     * @param bo 查询条件
     * @return 场景联动触发器列表
     */
    @Override
    public List<SceneTriggerVo> queryList(SceneTriggerBo bo) {
        LambdaQueryWrapper<SceneTrigger> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SceneTrigger> buildQueryWrapper(SceneTriggerBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SceneTrigger> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getSceneId() != null, SceneTrigger::getSceneId, bo.getSceneId());
        lqw.eq(bo.getDeviceId() != null, SceneTrigger::getDeviceId, bo.getDeviceId());
        lqw.eq(bo.getProductId() != null, SceneTrigger::getProductId, bo.getProductId());
        lqw.eq(bo.getStatus() != null, SceneTrigger::getStatus, bo.getStatus());
        lqw.eq(bo.getSource() != null, SceneTrigger::getSource, bo.getSource());
        lqw.eq(StringUtils.isNotBlank(bo.getModelId()), SceneTrigger::getModelId, bo.getModelId());
        lqw.like(StringUtils.isNotBlank(bo.getName()), SceneTrigger::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getValue()), SceneTrigger::getValue, bo.getValue());
        lqw.eq(StringUtils.isNotBlank(bo.getOperator()), SceneTrigger::getOperator, bo.getOperator());
        lqw.eq(bo.getType() != null, SceneTrigger::getType, bo.getType());
        return lqw;
    }

    /**
     * 新增场景联动触发器
     *
     * @param bo 场景联动触发器
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(SceneTriggerBo bo) {
        SceneTrigger add = MapstructUtils.convert(bo, SceneTrigger.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改场景联动触发器
     *
     * @param bo 场景联动触发器
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(SceneTriggerBo bo) {
        SceneTrigger update = MapstructUtils.convert(bo, SceneTrigger.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SceneTrigger entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除场景联动触发器信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}
