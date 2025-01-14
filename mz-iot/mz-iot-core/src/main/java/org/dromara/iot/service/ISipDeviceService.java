package org.dromara.iot.service;

import org.dromara.iot.domain.vo.SipDeviceVo;
import org.dromara.iot.domain.bo.SipDeviceBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 监控设备Service接口
 *
 * @author MzMedia
 * @date 2025-01-14
 */
public interface ISipDeviceService {

    /**
     * 查询监控设备
     *
     * @param id 主键
     * @return 监控设备
     */
    SipDeviceVo queryById(Long id);

    /**
     * 分页查询监控设备列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 监控设备分页列表
     */
    TableDataInfo<SipDeviceVo> queryPageList(SipDeviceBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的监控设备列表
     *
     * @param bo 查询条件
     * @return 监控设备列表
     */
    List<SipDeviceVo> queryList(SipDeviceBo bo);

    /**
     * 新增监控设备
     *
     * @param bo 监控设备
     * @return 是否新增成功
     */
    Boolean insertByBo(SipDeviceBo bo);

    /**
     * 修改监控设备
     *
     * @param bo 监控设备
     * @return 是否修改成功
     */
    Boolean updateByBo(SipDeviceBo bo);

    /**
     * 校验并批量删除监控设备信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
