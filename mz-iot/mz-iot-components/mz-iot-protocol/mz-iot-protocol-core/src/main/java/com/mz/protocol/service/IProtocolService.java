package com.mz.protocol.service;

import com.mz.protocol.domain.vo.ProtocolVo;
import com.mz.protocol.domain.bo.ProtocolBo;
import com.mz.common.mybatis.core.page.TableDataInfo;
import com.mz.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 协议Service接口
 *
 * @author MzMedia
 * @date 2023-12-25
 */
public interface IProtocolService {

    /**
     * 查询协议
     */
    ProtocolVo queryById(Long id);

    /**
     * 查询协议列表
     */
    TableDataInfo<ProtocolVo> queryPageList(ProtocolBo bo, PageQuery pageQuery);

    /**
     * 查询协议列表
     */
    List<ProtocolVo> queryList(ProtocolBo bo);

    /**
     * 新增协议
     */
    Boolean insertByBo(ProtocolBo bo);

    /**
     * 修改协议
     */
    Boolean updateByBo(ProtocolBo bo);

    /**
     * 校验并批量删除协议信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
