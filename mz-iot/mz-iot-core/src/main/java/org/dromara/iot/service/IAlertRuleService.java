package org.dromara.iot.service;

import org.dromara.iot.domain.vo.AlertRuleVo;
import org.dromara.iot.domain.bo.AlertRuleBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 设备告警规则Service接口
 *
 * @author MzMedia
 * @date 2025-01-14
 */
public interface IAlertRuleService {

    /**
     * 查询设备告警规则
     *
     * @param id 主键
     * @return 设备告警规则
     */
    AlertRuleVo queryById(Long id);

    /**
     * 分页查询设备告警规则列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 设备告警规则分页列表
     */
    TableDataInfo<AlertRuleVo> queryPageList(AlertRuleBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的设备告警规则列表
     *
     * @param bo 查询条件
     * @return 设备告警规则列表
     */
    List<AlertRuleVo> queryList(AlertRuleBo bo);

    /**
     * 新增设备告警规则
     *
     * @param bo 设备告警规则
     * @return 是否新增成功
     */
    Boolean insertByBo(AlertRuleBo bo);

    /**
     * 修改设备告警规则
     *
     * @param bo 设备告警规则
     * @return 是否修改成功
     */
    Boolean updateByBo(AlertRuleBo bo);

    /**
     * 校验并批量删除设备告警规则信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
