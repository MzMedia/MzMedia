package com.mz.iot.service;

import com.mz.iot.domain.AlertRule;
import com.mz.iot.domain.vo.AlertRuleVo;
import com.mz.iot.domain.bo.AlertRuleBo;
import com.mz.common.mybatis.core.page.TableDataInfo;
import com.mz.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 设备告警规则Service接口
 *
 * @author MzMedia
 * @date 2023-10-18
 */
public interface IAlertRuleService {

    /**
     * 查询设备告警规则
     */
    AlertRuleVo queryById(Long alertId);

    /**
     * 查询设备告警规则列表
     */
    TableDataInfo<AlertRuleVo> queryPageList(AlertRuleBo bo, PageQuery pageQuery);

    /**
     * 查询设备告警规则列表
     */
    List<AlertRuleVo> queryList(AlertRuleBo bo);

    /**
     * 新增设备告警规则
     */
    Boolean insertByBo(AlertRuleBo bo);

    /**
     * 修改设备告警规则
     */
    Boolean updateByBo(AlertRuleBo bo);

    /**
     * 校验并批量删除设备告警规则信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
