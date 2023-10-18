package com.mz.iot.service;

import com.mz.iot.domain.AlertLog;
import com.mz.iot.domain.vo.AlertLogVo;
import com.mz.iot.domain.bo.AlertLogBo;
import com.mz.common.mybatis.core.page.TableDataInfo;
import com.mz.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 设备告警日志Service接口
 *
 * @author MzMedia
 * @date 2023-10-18
 */
public interface IAlertLogService {

    /**
     * 查询设备告警日志
     */
    AlertLogVo queryById(Long alertLogId);

    /**
     * 查询设备告警日志列表
     */
    TableDataInfo<AlertLogVo> queryPageList(AlertLogBo bo, PageQuery pageQuery);

    /**
     * 查询设备告警日志列表
     */
    List<AlertLogVo> queryList(AlertLogBo bo);

    /**
     * 新增设备告警日志
     */
    Boolean insertByBo(AlertLogBo bo);

    /**
     * 修改设备告警日志
     */
    Boolean updateByBo(AlertLogBo bo);

    /**
     * 校验并批量删除设备告警日志信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
