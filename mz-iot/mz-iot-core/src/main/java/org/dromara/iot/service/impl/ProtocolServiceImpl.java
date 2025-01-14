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
import org.dromara.iot.domain.bo.ProtocolBo;
import org.dromara.iot.domain.vo.ProtocolVo;
import org.dromara.iot.domain.Protocol;
import org.dromara.iot.mapper.ProtocolMapper;
import org.dromara.iot.service.IProtocolService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 协议Service业务层处理
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@RequiredArgsConstructor
@Service
public class ProtocolServiceImpl implements IProtocolService {

    private final ProtocolMapper baseMapper;

    /**
     * 查询协议
     *
     * @param id 主键
     * @return 协议
     */
    @Override
    public ProtocolVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询协议列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 协议分页列表
     */
    @Override
    public TableDataInfo<ProtocolVo> queryPageList(ProtocolBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Protocol> lqw = buildQueryWrapper(bo);
        Page<ProtocolVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的协议列表
     *
     * @param bo 查询条件
     * @return 协议列表
     */
    @Override
    public List<ProtocolVo> queryList(ProtocolBo bo) {
        LambdaQueryWrapper<Protocol> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Protocol> buildQueryWrapper(ProtocolBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Protocol> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getName()), Protocol::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getJarFile()), Protocol::getJarFile, bo.getJarFile());
        lqw.eq(StringUtils.isNotBlank(bo.getCodecFile()), Protocol::getCodecFile, bo.getCodecFile());
        lqw.eq(StringUtils.isNotBlank(bo.getNetwork()), Protocol::getNetwork, bo.getNetwork());
        lqw.eq(StringUtils.isNotBlank(bo.getConfig()), Protocol::getConfig, bo.getConfig());
        lqw.eq(bo.getCodecId() != null, Protocol::getCodecId, bo.getCodecId());
        lqw.eq(StringUtils.isNotBlank(bo.getCodecType()), Protocol::getCodecType, bo.getCodecType());
        lqw.eq(StringUtils.isNotBlank(bo.getScript()), Protocol::getScript, bo.getScript());
        lqw.eq(StringUtils.isNotBlank(bo.getScriptLang()), Protocol::getScriptLang, bo.getScriptLang());
        lqw.eq(StringUtils.isNotBlank(bo.getState()), Protocol::getState, bo.getState());
        return lqw;
    }

    /**
     * 新增协议
     *
     * @param bo 协议
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(ProtocolBo bo) {
        Protocol add = MapstructUtils.convert(bo, Protocol.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改协议
     *
     * @param bo 协议
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(ProtocolBo bo) {
        Protocol update = MapstructUtils.convert(bo, Protocol.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Protocol entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除协议信息
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
