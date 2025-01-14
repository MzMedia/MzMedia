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
import org.dromara.iot.domain.bo.SipDeviceChannelBo;
import org.dromara.iot.domain.vo.SipDeviceChannelVo;
import org.dromara.iot.domain.SipDeviceChannel;
import org.dromara.iot.mapper.SipDeviceChannelMapper;
import org.dromara.iot.service.ISipDeviceChannelService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 监控设备通道信息Service业务层处理
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@RequiredArgsConstructor
@Service
public class SipDeviceChannelServiceImpl implements ISipDeviceChannelService {

    private final SipDeviceChannelMapper baseMapper;

    /**
     * 查询监控设备通道信息
     *
     * @param id 主键
     * @return 监控设备通道信息
     */
    @Override
    public SipDeviceChannelVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询监控设备通道信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 监控设备通道信息分页列表
     */
    @Override
    public TableDataInfo<SipDeviceChannelVo> queryPageList(SipDeviceChannelBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SipDeviceChannel> lqw = buildQueryWrapper(bo);
        Page<SipDeviceChannelVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的监控设备通道信息列表
     *
     * @param bo 查询条件
     * @return 监控设备通道信息列表
     */
    @Override
    public List<SipDeviceChannelVo> queryList(SipDeviceChannelBo bo) {
        LambdaQueryWrapper<SipDeviceChannel> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SipDeviceChannel> buildQueryWrapper(SipDeviceChannelBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SipDeviceChannel> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getProductId() != null, SipDeviceChannel::getProductId, bo.getProductId());
        lqw.like(StringUtils.isNotBlank(bo.getProductName()), SipDeviceChannel::getProductName, bo.getProductName());
        lqw.eq(StringUtils.isNotBlank(bo.getDeviceSipId()), SipDeviceChannel::getDeviceSipId, bo.getDeviceSipId());
        lqw.eq(StringUtils.isNotBlank(bo.getChannelSipId()), SipDeviceChannel::getChannelSipId, bo.getChannelSipId());
        lqw.like(StringUtils.isNotBlank(bo.getChannelName()), SipDeviceChannel::getChannelName, bo.getChannelName());
        lqw.eq(bo.getRegisterTime() != null, SipDeviceChannel::getRegisterTime, bo.getRegisterTime());
        lqw.eq(StringUtils.isNotBlank(bo.getDeviceType()), SipDeviceChannel::getDeviceType, bo.getDeviceType());
        lqw.eq(StringUtils.isNotBlank(bo.getChannelType()), SipDeviceChannel::getChannelType, bo.getChannelType());
        lqw.eq(StringUtils.isNotBlank(bo.getCityCode()), SipDeviceChannel::getCityCode, bo.getCityCode());
        lqw.eq(StringUtils.isNotBlank(bo.getCivilCode()), SipDeviceChannel::getCivilCode, bo.getCivilCode());
        lqw.eq(StringUtils.isNotBlank(bo.getManufacture()), SipDeviceChannel::getManufacture, bo.getManufacture());
        lqw.eq(StringUtils.isNotBlank(bo.getModel()), SipDeviceChannel::getModel, bo.getModel());
        lqw.eq(StringUtils.isNotBlank(bo.getOwner()), SipDeviceChannel::getOwner, bo.getOwner());
        lqw.eq(StringUtils.isNotBlank(bo.getBlock()), SipDeviceChannel::getBlock, bo.getBlock());
        lqw.eq(StringUtils.isNotBlank(bo.getAddress()), SipDeviceChannel::getAddress, bo.getAddress());
        lqw.eq(StringUtils.isNotBlank(bo.getParentId()), SipDeviceChannel::getParentId, bo.getParentId());
        lqw.eq(StringUtils.isNotBlank(bo.getIpAddress()), SipDeviceChannel::getIpAddress, bo.getIpAddress());
        lqw.eq(bo.getPort() != null, SipDeviceChannel::getPort, bo.getPort());
        lqw.eq(StringUtils.isNotBlank(bo.getPassword()), SipDeviceChannel::getPassword, bo.getPassword());
        lqw.eq(bo.getPTZType() != null, SipDeviceChannel::getPTZType, bo.getPTZType());
        lqw.eq(StringUtils.isNotBlank(bo.getPTZTypeText()), SipDeviceChannel::getPTZTypeText, bo.getPTZTypeText());
        lqw.eq(bo.getStatus() != null, SipDeviceChannel::getStatus, bo.getStatus());
        lqw.eq(bo.getLongitude() != null, SipDeviceChannel::getLongitude, bo.getLongitude());
        lqw.eq(bo.getLatitude() != null, SipDeviceChannel::getLatitude, bo.getLatitude());
        lqw.eq(StringUtils.isNotBlank(bo.getStreamId()), SipDeviceChannel::getStreamId, bo.getStreamId());
        lqw.eq(bo.getSubCount() != null, SipDeviceChannel::getSubCount, bo.getSubCount());
        lqw.eq(bo.getParental() != null, SipDeviceChannel::getParental, bo.getParental());
        lqw.eq(bo.getHasAudio() != null, SipDeviceChannel::getHasAudio, bo.getHasAudio());
        return lqw;
    }

    /**
     * 新增监控设备通道信息
     *
     * @param bo 监控设备通道信息
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(SipDeviceChannelBo bo) {
        SipDeviceChannel add = MapstructUtils.convert(bo, SipDeviceChannel.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改监控设备通道信息
     *
     * @param bo 监控设备通道信息
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(SipDeviceChannelBo bo) {
        SipDeviceChannel update = MapstructUtils.convert(bo, SipDeviceChannel.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SipDeviceChannel entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除监控设备通道信息信息
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
