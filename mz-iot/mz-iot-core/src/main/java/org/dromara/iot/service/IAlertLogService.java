package org.dromara.iot.service;

import org.dromara.iot.domain.vo.AlertLogVo;
import org.dromara.iot.domain.bo.AlertLogBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 设备告警日志Service接口
 *
 * @author MzMedia
 * @date 2025-01-14
 */
public interface IAlertLogService {

    /**
     * 查询设备告警日志
     *
     * @param id 主键
     * @return 设备告警日志
     */
    AlertLogVo queryById(Long id);

    /**
     * 分页查询设备告警日志列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 设备告警日志分页列表
     */
    TableDataInfo<AlertLogVo> queryPageList(AlertLogBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的设备告警日志列表
     *
     * @param bo 查询条件
     * @return 设备告警日志列表
     */
    List<AlertLogVo> queryList(AlertLogBo bo);

    /**
     * 新增设备告警日志
     *
     * @param bo 设备告警日志
     * @return 是否新增成功
     */
    Boolean insertByBo(AlertLogBo bo);

    /**
     * 修改设备告警日志
     *
     * @param bo 设备告警日志
     * @return 是否修改成功
     */
    Boolean updateByBo(AlertLogBo bo);

    /**
     * 校验并批量删除设备告警日志信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
