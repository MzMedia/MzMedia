package org.dromara.iot.service;

import org.dromara.iot.domain.vo.ProtocolCodecVo;
import org.dromara.iot.domain.bo.ProtocolCodecBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 协议编解码器Service接口
 *
 * @author MzMedia
 * @date 2025-01-14
 */
public interface IProtocolCodecService {

    /**
     * 查询协议编解码器
     *
     * @param id 主键
     * @return 协议编解码器
     */
    ProtocolCodecVo queryById(Long id);

    /**
     * 分页查询协议编解码器列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 协议编解码器分页列表
     */
    TableDataInfo<ProtocolCodecVo> queryPageList(ProtocolCodecBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的协议编解码器列表
     *
     * @param bo 查询条件
     * @return 协议编解码器列表
     */
    List<ProtocolCodecVo> queryList(ProtocolCodecBo bo);

    /**
     * 新增协议编解码器
     *
     * @param bo 协议编解码器
     * @return 是否新增成功
     */
    Boolean insertByBo(ProtocolCodecBo bo);

    /**
     * 修改协议编解码器
     *
     * @param bo 协议编解码器
     * @return 是否修改成功
     */
    Boolean updateByBo(ProtocolCodecBo bo);

    /**
     * 校验并批量删除协议编解码器信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
