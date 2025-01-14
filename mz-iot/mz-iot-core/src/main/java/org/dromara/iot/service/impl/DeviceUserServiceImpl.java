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
import org.dromara.iot.domain.bo.DeviceUserBo;
import org.dromara.iot.domain.vo.DeviceUserVo;
import org.dromara.iot.domain.DeviceUser;
import org.dromara.iot.mapper.DeviceUserMapper;
import org.dromara.iot.service.IDeviceUserService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 设备用户Service业务层处理
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@RequiredArgsConstructor
@Service
public class DeviceUserServiceImpl implements IDeviceUserService {

    private final DeviceUserMapper baseMapper;

    /**
     * 查询设备用户
     *
     * @param id 主键
     * @return 设备用户
     */
    @Override
    public DeviceUserVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询设备用户列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 设备用户分页列表
     */
    @Override
    public TableDataInfo<DeviceUserVo> queryPageList(DeviceUserBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DeviceUser> lqw = buildQueryWrapper(bo);
        Page<DeviceUserVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的设备用户列表
     *
     * @param bo 查询条件
     * @return 设备用户列表
     */
    @Override
    public List<DeviceUserVo> queryList(DeviceUserBo bo) {
        LambdaQueryWrapper<DeviceUser> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DeviceUser> buildQueryWrapper(DeviceUserBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DeviceUser> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUserId() != null, DeviceUser::getUserId, bo.getUserId());
        lqw.like(StringUtils.isNotBlank(bo.getUserName()), DeviceUser::getUserName, bo.getUserName());
        lqw.eq(bo.getDeviceId() != null, DeviceUser::getDeviceId, bo.getDeviceId());
        lqw.eq(StringUtils.isNotBlank(bo.getPhonenumber()), DeviceUser::getPhonenumber, bo.getPhonenumber());
        lqw.eq(bo.getIsOwner() != null, DeviceUser::getIsOwner, bo.getIsOwner());
        lqw.eq(StringUtils.isNotBlank(bo.getPerms()), DeviceUser::getPerms, bo.getPerms());
        return lqw;
    }

    /**
     * 新增设备用户
     *
     * @param bo 设备用户
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(DeviceUserBo bo) {
        DeviceUser add = MapstructUtils.convert(bo, DeviceUser.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改设备用户
     *
     * @param bo 设备用户
     * @return 是否修改成功
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
     * 校验并批量删除设备用户信息
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
