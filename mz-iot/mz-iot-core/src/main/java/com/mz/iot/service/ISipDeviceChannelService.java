package com.mz.iot.service;

import com.mz.iot.domain.SipDeviceChannel;
import com.mz.iot.domain.vo.SipDeviceChannelVo;
import com.mz.iot.domain.bo.SipDeviceChannelBo;
import com.mz.common.mybatis.core.page.TableDataInfo;
import com.mz.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 监控设备通道信息Service接口
 *
 * @author MzMedia
 * @date 2023-10-18
 */
public interface ISipDeviceChannelService {

    /**
     * 查询监控设备通道信息
     */
    SipDeviceChannelVo queryById(Long id);

    /**
     * 查询监控设备通道信息列表
     */
    TableDataInfo<SipDeviceChannelVo> queryPageList(SipDeviceChannelBo bo, PageQuery pageQuery);

    /**
     * 查询监控设备通道信息列表
     */
    List<SipDeviceChannelVo> queryList(SipDeviceChannelBo bo);

    /**
     * 新增监控设备通道信息
     */
    Boolean insertByBo(SipDeviceChannelBo bo);

    /**
     * 修改监控设备通道信息
     */
    Boolean updateByBo(SipDeviceChannelBo bo);

    /**
     * 校验并批量删除监控设备通道信息信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
