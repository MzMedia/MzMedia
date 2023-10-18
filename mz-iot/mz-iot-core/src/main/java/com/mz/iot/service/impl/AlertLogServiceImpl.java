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
import com.mz.iot.domain.bo.AlertLogBo;
import com.mz.iot.domain.vo.AlertLogVo;
import com.mz.iot.domain.AlertLog;
import com.mz.iot.mapper.AlertLogMapper;
import com.mz.iot.service.IAlertLogService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 设备告警日志Service业务层处理
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@RequiredArgsConstructor
@Service
public class AlertLogServiceImpl implements IAlertLogService {

    private final AlertLogMapper baseMapper;

    /**
     * 查询设备告警日志
     */
    @Override
    public AlertLogVo queryById(Long alertLogId){
        return baseMapper.selectVoById(alertLogId);
    }

    /**
     * 查询设备告警日志列表
     */
    @Override
    public TableDataInfo<AlertLogVo> queryPageList(AlertLogBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<AlertLog> lqw = buildQueryWrapper(bo);
        Page<AlertLogVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询设备告警日志列表
     */
    @Override
    public List<AlertLogVo> queryList(AlertLogBo bo) {
        LambdaQueryWrapper<AlertLog> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<AlertLog> buildQueryWrapper(AlertLogBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<AlertLog> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, AlertLog::getDeviceId, bo.getDeviceId());
        lqw.eq(bo.getProductId() != null, AlertLog::getProductId, bo.getProductId());
        lqw.like(StringUtils.isNotBlank(bo.getAlertName()), AlertLog::getAlertName, bo.getAlertName());
        lqw.eq(bo.getAlertLevel() != null, AlertLog::getAlertLevel, bo.getAlertLevel());
        lqw.eq(StringUtils.isNotBlank(bo.getDetail()), AlertLog::getDetail, bo.getDetail());
        lqw.eq(bo.getStatus() != null, AlertLog::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增设备告警日志
     */
    @Override
    public Boolean insertByBo(AlertLogBo bo) {
        AlertLog add = MapstructUtils.convert(bo, AlertLog.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setAlertLogId(add.getAlertLogId());
        }
        return flag;
    }

    /**
     * 修改设备告警日志
     */
    @Override
    public Boolean updateByBo(AlertLogBo bo) {
        AlertLog update = MapstructUtils.convert(bo, AlertLog.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(AlertLog entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除设备告警日志
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
