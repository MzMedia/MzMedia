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
import org.dromara.iot.domain.bo.AlertRuleBo;
import org.dromara.iot.domain.vo.AlertRuleVo;
import org.dromara.iot.domain.AlertRule;
import org.dromara.iot.mapper.AlertRuleMapper;
import org.dromara.iot.service.IAlertRuleService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 设备告警规则Service业务层处理
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@RequiredArgsConstructor
@Service
public class AlertRuleServiceImpl implements IAlertRuleService {

    private final AlertRuleMapper baseMapper;

    /**
     * 查询设备告警规则
     *
     * @param id 主键
     * @return 设备告警规则
     */
    @Override
    public AlertRuleVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询设备告警规则列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 设备告警规则分页列表
     */
    @Override
    public TableDataInfo<AlertRuleVo> queryPageList(AlertRuleBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<AlertRule> lqw = buildQueryWrapper(bo);
        Page<AlertRuleVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的设备告警规则列表
     *
     * @param bo 查询条件
     * @return 设备告警规则列表
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
     *
     * @param bo 设备告警规则
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(AlertRuleBo bo) {
        AlertRule add = MapstructUtils.convert(bo, AlertRule.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改设备告警规则
     *
     * @param bo 设备告警规则
     * @return 是否修改成功
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
     * 校验并批量删除设备告警规则信息
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
