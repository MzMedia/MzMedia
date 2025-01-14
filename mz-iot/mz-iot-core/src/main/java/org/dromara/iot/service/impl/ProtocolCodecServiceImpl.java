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
import org.dromara.iot.domain.bo.ProtocolCodecBo;
import org.dromara.iot.domain.vo.ProtocolCodecVo;
import org.dromara.iot.domain.ProtocolCodec;
import org.dromara.iot.mapper.ProtocolCodecMapper;
import org.dromara.iot.service.IProtocolCodecService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 协议编解码器Service业务层处理
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@RequiredArgsConstructor
@Service
public class ProtocolCodecServiceImpl implements IProtocolCodecService {

    private final ProtocolCodecMapper baseMapper;

    /**
     * 查询协议编解码器
     *
     * @param id 主键
     * @return 协议编解码器
     */
    @Override
    public ProtocolCodecVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询协议编解码器列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 协议编解码器分页列表
     */
    @Override
    public TableDataInfo<ProtocolCodecVo> queryPageList(ProtocolCodecBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ProtocolCodec> lqw = buildQueryWrapper(bo);
        Page<ProtocolCodecVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的协议编解码器列表
     *
     * @param bo 查询条件
     * @return 协议编解码器列表
     */
    @Override
    public List<ProtocolCodecVo> queryList(ProtocolCodecBo bo) {
        LambdaQueryWrapper<ProtocolCodec> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ProtocolCodec> buildQueryWrapper(ProtocolCodecBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ProtocolCodec> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getName()), ProtocolCodec::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getDescribe()), ProtocolCodec::getDescribe, bo.getDescribe());
        lqw.eq(StringUtils.isNotBlank(bo.getCodecType()), ProtocolCodec::getCodecType, bo.getCodecType());
        lqw.eq(StringUtils.isNotBlank(bo.getScript()), ProtocolCodec::getScript, bo.getScript());
        lqw.eq(StringUtils.isNotBlank(bo.getScriptLang()), ProtocolCodec::getScriptLang, bo.getScriptLang());
        return lqw;
    }

    /**
     * 新增协议编解码器
     *
     * @param bo 协议编解码器
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(ProtocolCodecBo bo) {
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
     *
     * @param bo 协议编解码器
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(ProtocolCodecBo bo) {
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
     * 校验并批量删除协议编解码器信息
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
