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
import org.dromara.iot.domain.bo.SipDeviceBo;
import org.dromara.iot.domain.vo.SipDeviceVo;
import org.dromara.iot.domain.SipDevice;
import org.dromara.iot.mapper.SipDeviceMapper;
import org.dromara.iot.service.ISipDeviceService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 监控设备Service业务层处理
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@RequiredArgsConstructor
@Service
public class SipDeviceServiceImpl implements ISipDeviceService {

    private final SipDeviceMapper baseMapper;

    /**
     * 查询监控设备
     *
     * @param id 主键
     * @return 监控设备
     */
    @Override
    public SipDeviceVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询监控设备列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 监控设备分页列表
     */
    @Override
    public TableDataInfo<SipDeviceVo> queryPageList(SipDeviceBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SipDevice> lqw = buildQueryWrapper(bo);
        Page<SipDeviceVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的监控设备列表
     *
     * @param bo 查询条件
     * @return 监控设备列表
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
     *
     * @param bo 监控设备
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(SipDeviceBo bo) {
        SipDevice add = MapstructUtils.convert(bo, SipDevice.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改监控设备
     *
     * @param bo 监控设备
     * @return 是否修改成功
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
     * 校验并批量删除监控设备信息
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
