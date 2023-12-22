package com.mz.protocol.service;

import com.mz.protocol.domain.vo.ProtocolCcdecVo;
import com.mz.protocol.domain.bo.ProtocolCcdecBo;
import com.mz.common.mybatis.core.page.TableDataInfo;
import com.mz.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 协议编解码器Service接口
 *
 * @author MzMedia
 * @date 2023-12-22
 */
public interface IProtocolCcdecService {

    /**
     * 查询协议编解码器
     */
    ProtocolCcdecVo queryById(Long id);

    /**
     * 查询协议编解码器列表
     */
    TableDataInfo<ProtocolCcdecVo> queryPageList(ProtocolCcdecBo bo, PageQuery pageQuery);

    /**
     * 查询协议编解码器列表
     */
    List<ProtocolCcdecVo> queryList(ProtocolCcdecBo bo);

    /**
     * 新增协议编解码器
     */
    Boolean insertByBo(ProtocolCcdecBo bo);

    /**
     * 修改协议编解码器
     */
    Boolean updateByBo(ProtocolCcdecBo bo);

    /**
     * 校验并批量删除协议编解码器信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
