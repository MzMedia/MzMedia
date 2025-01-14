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
import org.dromara.iot.domain.bo.AlertLogBo;
import org.dromara.iot.domain.vo.AlertLogVo;
import org.dromara.iot.domain.AlertLog;
import org.dromara.iot.mapper.AlertLogMapper;
import org.dromara.iot.service.IAlertLogService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 设备告警日志Service业务层处理
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@RequiredArgsConstructor
@Service
public class AlertLogServiceImpl implements IAlertLogService {

    private final AlertLogMapper baseMapper;

    /**
     * 查询设备告警日志
     *
     * @param id 主键
     * @return 设备告警日志
     */
    @Override
    public AlertLogVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询设备告警日志列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 设备告警日志分页列表
     */
    @Override
    public TableDataInfo<AlertLogVo> queryPageList(AlertLogBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<AlertLog> lqw = buildQueryWrapper(bo);
        Page<AlertLogVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的设备告警日志列表
     *
     * @param bo 查询条件
     * @return 设备告警日志列表
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
     *
     * @param bo 设备告警日志
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(AlertLogBo bo) {
        AlertLog add = MapstructUtils.convert(bo, AlertLog.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改设备告警日志
     *
     * @param bo 设备告警日志
     * @return 是否修改成功
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
     * 校验并批量删除设备告警日志信息
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
