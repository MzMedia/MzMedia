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
import com.mz.iot.domain.bo.SipDeviceBo;
import com.mz.iot.domain.vo.SipDeviceVo;
import com.mz.iot.domain.SipDevice;
import com.mz.iot.mapper.SipDeviceMapper;
import com.mz.iot.service.ISipDeviceService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 监控设备Service业务层处理
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@RequiredArgsConstructor
@Service
public class SipDeviceServiceImpl implements ISipDeviceService {

    private final SipDeviceMapper baseMapper;

    /**
     * 查询监控设备
     */
    @Override
    public SipDeviceVo queryById(Long deviceId){
        return baseMapper.selectVoById(deviceId);
    }

    /**
     * 查询监控设备列表
     */
    @Override
    public TableDataInfo<SipDeviceVo> queryPageList(SipDeviceBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SipDevice> lqw = buildQueryWrapper(bo);
        Page<SipDeviceVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询监控设备列表
     */
    @Override
    public List<SipDeviceVo> queryList(SipDeviceBo bo) {
        LambdaQueryWrapper<SipDevice> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SipDevice> buildQueryWrapper(SipDeviceBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SipDevice> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getProductId() != null, SipDevice::getProductId, bo.getProductId());
        lqw.eq(StringUtils.isNotBlank(bo.getDeviceSipId()), SipDevice::getDeviceSipId, bo.getDeviceSipId());
        lqw.like(StringUtils.isNotBlank(bo.getDeviceName()), SipDevice::getDeviceName, bo.getDeviceName());
        lqw.eq(StringUtils.isNotBlank(bo.getManufacturer()), SipDevice::getManufacturer, bo.getManufacturer());
        lqw.eq(StringUtils.isNotBlank(bo.getModel()), SipDevice::getModel, bo.getModel());
        lqw.eq(StringUtils.isNotBlank(bo.getFirmware()), SipDevice::getFirmware, bo.getFirmware());
        lqw.eq(StringUtils.isNotBlank(bo.getTransport()), SipDevice::getTransport, bo.getTransport());
        lqw.eq(StringUtils.isNotBlank(bo.getStreamMode()), SipDevice::getStreamMode, bo.getStreamMode());
        lqw.eq(StringUtils.isNotBlank(bo.getOnline()), SipDevice::getOnline, bo.getOnline());
        lqw.eq(bo.getRegisterTime() != null, SipDevice::getRegisterTime, bo.getRegisterTime());
        lqw.eq(bo.getLastConnectTime() != null, SipDevice::getLastConnectTime, bo.getLastConnectTime());
        lqw.eq(bo.getActiveTime() != null, SipDevice::getActiveTime, bo.getActiveTime());
        lqw.eq(StringUtils.isNotBlank(bo.getIp()), SipDevice::getIp, bo.getIp());
        lqw.eq(bo.getPort() != null, SipDevice::getPort, bo.getPort());
        lqw.eq(StringUtils.isNotBlank(bo.getHostAddress()), SipDevice::getHostAddress, bo.getHostAddress());
        return lqw;
    }

    /**
     * 新增监控设备
     */
    @Override
    public Boolean insertByBo(SipDeviceBo bo) {
        SipDevice add = MapstructUtils.convert(bo, SipDevice.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setDeviceId(add.getDeviceId());
        }
        return flag;
    }

    /**
     * 修改监控设备
     */
    @Override
    public Boolean updateByBo(SipDeviceBo bo) {
        SipDevice update = MapstructUtils.convert(bo, SipDevice.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SipDevice entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除监控设备
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
