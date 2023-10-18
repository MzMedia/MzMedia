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
import com.mz.iot.domain.bo.DeviceUserBo;
import com.mz.iot.domain.vo.DeviceUserVo;
import com.mz.iot.domain.DeviceUser;
import com.mz.iot.mapper.DeviceUserMapper;
import com.mz.iot.service.IDeviceUserService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 设备用户Service业务层处理
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@RequiredArgsConstructor
@Service
public class DeviceUserServiceImpl implements IDeviceUserService {

    private final DeviceUserMapper baseMapper;

    /**
     * 查询设备用户
     */
    @Override
    public DeviceUserVo queryById(Long userId){
        return baseMapper.selectVoById(userId);
    }

    /**
     * 查询设备用户列表
     */
    @Override
    public TableDataInfo<DeviceUserVo> queryPageList(DeviceUserBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DeviceUser> lqw = buildQueryWrapper(bo);
        Page<DeviceUserVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询设备用户列表
     */
    @Override
    public List<DeviceUserVo> queryList(DeviceUserBo bo) {
        LambdaQueryWrapper<DeviceUser> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DeviceUser> buildQueryWrapper(DeviceUserBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DeviceUser> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getUserName()), DeviceUser::getUserName, bo.getUserName());
        lqw.eq(StringUtils.isNotBlank(bo.getPhonenumber()), DeviceUser::getPhonenumber, bo.getPhonenumber());
        lqw.eq(bo.getIsOwner() != null, DeviceUser::getIsOwner, bo.getIsOwner());
        lqw.eq(StringUtils.isNotBlank(bo.getPerms()), DeviceUser::getPerms, bo.getPerms());
        return lqw;
    }

    /**
     * 新增设备用户
     */
    @Override
    public Boolean insertByBo(DeviceUserBo bo) {
        DeviceUser add = MapstructUtils.convert(bo, DeviceUser.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setUserId(add.getUserId());
        }
        return flag;
    }

    /**
     * 修改设备用户
     */
    @Override
    public Boolean updateByBo(DeviceUserBo bo) {
        DeviceUser update = MapstructUtils.convert(bo, DeviceUser.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DeviceUser entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除设备用户
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
