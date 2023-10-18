package com.mz.iot.service.impl;

import com.mz.common.core.utils.MapstructUtils;
import com.mz.common.mybatis.core.page.TableDataInfo;
import com.mz.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.mz.common.core.utils.StringUtils;
import com.mz.iot.domain.bo.AlertRuleBo;
import com.mz.iot.domain.vo.AlertRuleVo;
import com.mz.iot.domain.AlertRule;
import com.mz.iot.mapper.AlertRuleMapper;
import com.mz.iot.service.IAlertRuleService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 设备告警规则Service业务层处理
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@RequiredArgsConstructor
@Service
public class AlertRuleServiceImpl implements IAlertRuleService {

    private final AlertRuleMapper baseMapper;

    /**
     * 查询设备告警规则
     */
    @Override
    public AlertRuleVo queryById(Long alertId){
        return baseMapper.selectVoById(alertId);
    }

    /**
     * 查询设备告警规则列表
     */
    @Override
    public TableDataInfo<AlertRuleVo> queryPageList(AlertRuleBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<AlertRule> lqw = buildQueryWrapper(bo);
        Page<AlertRuleVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询设备告警规则列表
     */
    @Override
    public List<AlertRuleVo> queryList(AlertRuleBo bo) {
        LambdaQueryWrapper<AlertRule> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<AlertRule> buildQueryWrapper(AlertRuleBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<AlertRule> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getAlertName()), AlertRule::getAlertName, bo.getAlertName());
        lqw.eq(bo.getAlertLevel() != null, AlertRule::getAlertLevel, bo.getAlertLevel());
        lqw.eq(bo.getSceneId() != null, AlertRule::getSceneId, bo.getSceneId());
        return lqw;
    }

    /**
     * 新增设备告警规则
     */
    @Override
    public Boolean insertByBo(AlertRuleBo bo) {
        AlertRule add = MapstructUtils.convert(bo, AlertRule.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setAlertId(add.getAlertId());
        }
        return flag;
    }

    /**
     * 修改设备告警规则
     */
    @Override
    public Boolean updateByBo(AlertRuleBo bo) {
        AlertRule update = MapstructUtils.convert(bo, AlertRule.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(AlertRule entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除设备告警规则
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
