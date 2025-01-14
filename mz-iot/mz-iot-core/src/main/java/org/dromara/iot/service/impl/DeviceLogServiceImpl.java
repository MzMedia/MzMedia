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
import org.dromara.iot.domain.bo.DeviceLogBo;
import org.dromara.iot.domain.vo.DeviceLogVo;
import org.dromara.iot.domain.DeviceLog;
import org.dromara.iot.mapper.DeviceLogMapper;
import org.dromara.iot.service.IDeviceLogService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 设备日志Service业务层处理
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@RequiredArgsConstructor
@Service
public class DeviceLogServiceImpl implements IDeviceLogService {

    private final DeviceLogMapper baseMapper;

    /**
     * 查询设备日志
     *
     * @param id 主键
     * @return 设备日志
     */
    @Override
    public DeviceLogVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询设备日志列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 设备日志分页列表
     */
    @Override
    public TableDataInfo<DeviceLogVo> queryPageList(DeviceLogBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DeviceLog> lqw = buildQueryWrapper(bo);
        Page<DeviceLogVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的设备日志列表
     *
     * @param bo 查询条件
     * @return 设备日志列表
     */
    @Override
    public List<DeviceLogVo> queryList(DeviceLogBo bo) {
        LambdaQueryWrapper<DeviceLog> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DeviceLog> buildQueryWrapper(DeviceLogBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DeviceLog> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeviceId() != null, DeviceLog::getDeviceId, bo.getDeviceId());
        lqw.eq(StringUtils.isNotBlank(bo.getIdentity()), DeviceLog::getIdentity, bo.getIdentity());
        lqw.like(StringUtils.isNotBlank(bo.getModelName()), DeviceLog::getModelName, bo.getModelName());
        lqw.eq(bo.getLogType() != null, DeviceLog::getLogType, bo.getLogType());
        lqw.eq(StringUtils.isNotBlank(bo.getLogValue()), DeviceLog::getLogValue, bo.getLogValue());
        return lqw;
    }

    /**
     * 新增设备日志
     *
     * @param bo 设备日志
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(DeviceLogBo bo) {
        DeviceLog add = MapstructUtils.convert(bo, DeviceLog.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改设备日志
     *
     * @param bo 设备日志
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(DeviceLogBo bo) {
        DeviceLog update = MapstructUtils.convert(bo, DeviceLog.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DeviceLog entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除设备日志信息
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
