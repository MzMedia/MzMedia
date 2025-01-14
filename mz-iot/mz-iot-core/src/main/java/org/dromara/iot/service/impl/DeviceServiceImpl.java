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
import org.dromara.iot.domain.bo.DeviceBo;
import org.dromara.iot.domain.vo.DeviceVo;
import org.dromara.iot.domain.Device;
import org.dromara.iot.mapper.DeviceMapper;
import org.dromara.iot.service.IDeviceService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 设备信息Service业务层处理
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@RequiredArgsConstructor
@Service
public class DeviceServiceImpl implements IDeviceService {

    private final DeviceMapper baseMapper;

    /**
     * 查询设备信息
     *
     * @param id 主键
     * @return 设备信息
     */
    @Override
    public DeviceVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询设备信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 设备信息分页列表
     */
    @Override
    public TableDataInfo<DeviceVo> queryPageList(DeviceBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Device> lqw = buildQueryWrapper(bo);
        Page<DeviceVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的设备信息列表
     *
     * @param bo 查询条件
     * @return 设备信息列表
     */
    @Override
    public List<DeviceVo> queryList(DeviceBo bo) {
        LambdaQueryWrapper<Device> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Device> buildQueryWrapper(DeviceBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Device> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUserId() != null, Device::getUserId, bo.getUserId());
        lqw.eq(bo.getSubUid() != null, Device::getSubUid, bo.getSubUid());
        lqw.eq(bo.getSubDid() != null, Device::getSubDid, bo.getSubDid());
        lqw.like(StringUtils.isNotBlank(bo.getDeviceName()), Device::getDeviceName, bo.getDeviceName());
        lqw.like(StringUtils.isNotBlank(bo.getNickName()), Device::getNickName, bo.getNickName());
        lqw.eq(StringUtils.isNotBlank(bo.getProductKey()), Device::getProductKey, bo.getProductKey());
        lqw.eq(StringUtils.isNotBlank(bo.getSerialNumber()), Device::getSerialNumber, bo.getSerialNumber());
        lqw.eq(StringUtils.isNotBlank(bo.getSecret()), Device::getSecret, bo.getSecret());
        lqw.eq(StringUtils.isNotBlank(bo.getFirmwareVersion()), Device::getFirmwareVersion, bo.getFirmwareVersion());
        lqw.eq(StringUtils.isNotBlank(bo.getLocate()), Device::getLocate, bo.getLocate());
        lqw.eq(StringUtils.isNotBlank(bo.getTag()), Device::getTag, bo.getTag());
        lqw.eq(bo.getStatus() != null, Device::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getIp()), Device::getIp, bo.getIp());
        lqw.eq(bo.getActiveTime() != null, Device::getActiveTime, bo.getActiveTime());
        lqw.eq(StringUtils.isNotBlank(bo.getThingsModel()), Device::getThingsModel, bo.getThingsModel());
        lqw.eq(StringUtils.isNotBlank(bo.getSummary()), Device::getSummary, bo.getSummary());
        return lqw;
    }

    /**
     * 新增设备信息
     *
     * @param bo 设备信息
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(DeviceBo bo) {
        Device add = MapstructUtils.convert(bo, Device.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改设备信息
     *
     * @param bo 设备信息
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(DeviceBo bo) {
        Device update = MapstructUtils.convert(bo, Device.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Device entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除设备信息信息
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
