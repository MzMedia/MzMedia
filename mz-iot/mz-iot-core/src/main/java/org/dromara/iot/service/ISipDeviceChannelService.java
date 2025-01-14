package org.dromara.iot.service;

import org.dromara.iot.domain.vo.SipDeviceChannelVo;
import org.dromara.iot.domain.bo.SipDeviceChannelBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 监控设备通道信息Service接口
 *
 * @author MzMedia
 * @date 2025-01-14
 */
public interface ISipDeviceChannelService {

    /**
     * 查询监控设备通道信息
     *
     * @param id 主键
     * @return 监控设备通道信息
     */
    SipDeviceChannelVo queryById(Long id);

    /**
     * 分页查询监控设备通道信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 监控设备通道信息分页列表
     */
    TableDataInfo<SipDeviceChannelVo> queryPageList(SipDeviceChannelBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的监控设备通道信息列表
     *
     * @param bo 查询条件
     * @return 监控设备通道信息列表
     */
    List<SipDeviceChannelVo> queryList(SipDeviceChannelBo bo);

    /**
     * 新增监控设备通道信息
     *
     * @param bo 监控设备通道信息
     * @return 是否新增成功
     */
    Boolean insertByBo(SipDeviceChannelBo bo);

    /**
     * 修改监控设备通道信息
     *
     * @param bo 监控设备通道信息
     * @return 是否修改成功
     */
    Boolean updateByBo(SipDeviceChannelBo bo);

    /**
     * 校验并批量删除监控设备通道信息信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
