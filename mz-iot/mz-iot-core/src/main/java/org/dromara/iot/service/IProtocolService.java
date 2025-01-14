package org.dromara.iot.service;

import org.dromara.iot.domain.vo.ProtocolVo;
import org.dromara.iot.domain.bo.ProtocolBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 协议Service接口
 *
 * @author MzMedia
 * @date 2025-01-14
 */
public interface IProtocolService {

    /**
     * 查询协议
     *
     * @param id 主键
     * @return 协议
     */
    ProtocolVo queryById(Long id);

    /**
     * 分页查询协议列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 协议分页列表
     */
    TableDataInfo<ProtocolVo> queryPageList(ProtocolBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的协议列表
     *
     * @param bo 查询条件
     * @return 协议列表
     */
    List<ProtocolVo> queryList(ProtocolBo bo);

    /**
     * 新增协议
     *
     * @param bo 协议
     * @return 是否新增成功
     */
    Boolean insertByBo(ProtocolBo bo);

    /**
     * 修改协议
     *
     * @param bo 协议
     * @return 是否修改成功
     */
    Boolean updateByBo(ProtocolBo bo);

    /**
     * 校验并批量删除协议信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
