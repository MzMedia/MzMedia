package com.mz.protocol.service.impl;

import com.mz.common.core.utils.MapstructUtils;
import com.mz.common.mybatis.core.page.TableDataInfo;
import com.mz.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.mz.common.core.utils.StringUtils;
import com.mz.protocol.domain.bo.ProtocolCcdecBo;
import com.mz.protocol.domain.vo.ProtocolCcdecVo;
import com.mz.protocol.domain.ProtocolCodec;
import com.mz.protocol.mapper.ProtocolCcdecMapper;
import com.mz.protocol.service.IProtocolCcdecService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 协议编解码器Service业务层处理
 *
 * @author MzMedia
 * @date 2023-12-22
 */
@RequiredArgsConstructor
@Service
public class ProtocolCcdecServiceImpl implements IProtocolCcdecService {

    private final ProtocolCcdecMapper baseMapper;

    /**
     * 查询协议编解码器
     */
    @Override
    public ProtocolCcdecVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询协议编解码器列表
     */
    @Override
    public TableDataInfo<ProtocolCcdecVo> queryPageList(ProtocolCcdecBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ProtocolCodec> lqw = buildQueryWrapper(bo);
        Page<ProtocolCcdecVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询协议编解码器列表
     */
    @Override
    public List<ProtocolCcdecVo> queryList(ProtocolCcdecBo bo) {
        LambdaQueryWrapper<ProtocolCodec> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ProtocolCodec> buildQueryWrapper(ProtocolCcdecBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ProtocolCodec> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getName()), ProtocolCodec::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getDescribe()), ProtocolCodec::getDescribe, bo.getDescribe());
        lqw.eq(StringUtils.isNotBlank(bo.getScript()), ProtocolCodec::getScript, bo.getScript());
        lqw.eq(StringUtils.isNotBlank(bo.getScriptLang()), ProtocolCodec::getScriptLang, bo.getScriptLang());
        return lqw;
    }

    /**
     * 新增协议编解码器
     */
    @Override
    public Boolean insertByBo(ProtocolCcdecBo bo) {
        ProtocolCodec add = MapstructUtils.convert(bo, ProtocolCodec.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改协议编解码器
     */
    @Override
    public Boolean updateByBo(ProtocolCcdecBo bo) {
        ProtocolCodec update = MapstructUtils.convert(bo, ProtocolCodec.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ProtocolCodec entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除协议编解码器
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
