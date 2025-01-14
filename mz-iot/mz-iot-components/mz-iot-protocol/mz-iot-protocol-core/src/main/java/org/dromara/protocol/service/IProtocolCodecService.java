package org.dromara.protocol.service;

import org.dromara.protocol.domain.vo.ProtocolCodecVo;
import org.dromara.protocol.domain.bo.ProtocolCodecBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 协议编解码器Service接口
 *
 * @author MzMedia
 * @date 2023-12-25
 */
public interface IProtocolCodecService {

    /**
     * 查询协议编解码器
     */
    ProtocolCodecVo queryById(Long id);

    /**
     * 查询协议编解码器列表
     */
    TableDataInfo<ProtocolCodecVo> queryPageList(ProtocolCodecBo bo, PageQuery pageQuery);

    /**
     * 查询协议编解码器列表
     */
    List<ProtocolCodecVo> queryList(ProtocolCodecBo bo);

    /**
     * 新增协议编解码器
     */
    Boolean insertByBo(ProtocolCodecBo bo);

    /**
     * 修改协议编解码器
     */
    Boolean updateByBo(ProtocolCodecBo bo);

    /**
     * 校验并批量删除协议编解码器信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
