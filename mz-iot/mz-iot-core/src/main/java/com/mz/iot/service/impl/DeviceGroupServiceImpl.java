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
import com.mz.iot.domain.bo.DeviceGroupBo;
import com.mz.iot.domain.vo.DeviceGroupVo;
import com.mz.iot.domain.DeviceGroup;
import com.mz.iot.mapper.DeviceGroupMapper;
import com.mz.iot.service.IDeviceGroupService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 设备分组Service业务层处理
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@RequiredArgsConstructor
@Service
public class DeviceGroupServiceImpl implements IDeviceGroupService {

    private final DeviceGroupMapper baseMapper;

    /**
     * 查询设备分组
     */
    @Override
    public DeviceGroupVo queryById(Long deviceId){
        return baseMapper.selectVoById(deviceId);
    }

    /**
     * 查询设备分组列表
     */
    @Override
    public TableDataInfo<DeviceGroupVo> queryPageList(DeviceGroupBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DeviceGroup> lqw = buildQueryWrapper(bo);
        Page<DeviceGroupVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询设备分组列表
     */
    @Override
    public List<DeviceGroupVo> queryList(DeviceGroupBo bo) {
        LambdaQueryWrapper<DeviceGroup> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DeviceGroup> buildQueryWrapper(DeviceGroupBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DeviceGroup> lqw = Wrappers.lambdaQuery();
        return lqw;
    }

    /**
     * 新增设备分组
     */
    @Override
    public Boolean insertByBo(DeviceGroupBo bo) {
        DeviceGroup add = MapstructUtils.convert(bo, DeviceGroup.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setDeviceId(add.getDeviceId());
        }
        return flag;
    }

    /**
     * 修改设备分组
     */
    @Override
    public Boolean updateByBo(DeviceGroupBo bo) {
        DeviceGroup update = MapstructUtils.convert(bo, DeviceGroup.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DeviceGroup entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除设备分组
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
