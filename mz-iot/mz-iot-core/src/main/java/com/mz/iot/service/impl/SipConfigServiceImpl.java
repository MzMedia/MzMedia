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
import com.mz.iot.domain.bo.SipConfigBo;
import com.mz.iot.domain.vo.SipConfigVo;
import com.mz.iot.domain.SipConfig;
import com.mz.iot.mapper.SipConfigMapper;
import com.mz.iot.service.ISipConfigService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * sip系统配置Service业务层处理
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@RequiredArgsConstructor
@Service
public class SipConfigServiceImpl implements ISipConfigService {

    private final SipConfigMapper baseMapper;

    /**
     * 查询sip系统配置
     */
    @Override
    public SipConfigVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询sip系统配置列表
     */
    @Override
    public TableDataInfo<SipConfigVo> queryPageList(SipConfigBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SipConfig> lqw = buildQueryWrapper(bo);
        Page<SipConfigVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询sip系统配置列表
     */
    @Override
    public List<SipConfigVo> queryList(SipConfigBo bo) {
        LambdaQueryWrapper<SipConfig> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SipConfig> buildQueryWrapper(SipConfigBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SipConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getProductId() != null, SipConfig::getProductId, bo.getProductId());
        lqw.eq(bo.getEnabled() != null, SipConfig::getEnabled, bo.getEnabled());
        lqw.eq(bo.getIsdefault() != null, SipConfig::getIsdefault, bo.getIsdefault());
        lqw.eq(bo.getSeniorSdp() != null, SipConfig::getSeniorSdp, bo.getSeniorSdp());
        lqw.eq(StringUtils.isNotBlank(bo.getDomain()), SipConfig::getDomain, bo.getDomain());
        lqw.eq(StringUtils.isNotBlank(bo.getSipid()), SipConfig::getSipid, bo.getSipid());
        lqw.eq(StringUtils.isNotBlank(bo.getPassword()), SipConfig::getPassword, bo.getPassword());
        lqw.eq(StringUtils.isNotBlank(bo.getIp()), SipConfig::getIp, bo.getIp());
        lqw.eq(bo.getPort() != null, SipConfig::getPort, bo.getPort());
        return lqw;
    }

    /**
     * 新增sip系统配置
     */
    @Override
    public Boolean insertByBo(SipConfigBo bo) {
        SipConfig add = MapstructUtils.convert(bo, SipConfig.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改sip系统配置
     */
    @Override
    public Boolean updateByBo(SipConfigBo bo) {
        SipConfig update = MapstructUtils.convert(bo, SipConfig.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SipConfig entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除sip系统配置
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
