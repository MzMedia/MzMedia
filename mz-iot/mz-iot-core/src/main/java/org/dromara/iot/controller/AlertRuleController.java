package org.dromara.iot.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.iot.domain.vo.AlertRuleVo;
import org.dromara.iot.domain.bo.AlertRuleBo;
import org.dromara.iot.service.IAlertRuleService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 设备告警规则
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/alertRule")
public class AlertRuleController extends BaseController {

    private final IAlertRuleService alertRuleService;

    /**
     * 查询设备告警规则列表
     */
    @SaCheckPermission("iot:alertRule:list")
    @GetMapping("/list")
    public TableDataInfo<AlertRuleVo> list(AlertRuleBo bo, PageQuery pageQuery) {
        return alertRuleService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出设备告警规则列表
     */
    @SaCheckPermission("iot:alertRule:export")
    @Log(title = "设备告警规则", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(AlertRuleBo bo, HttpServletResponse response) {
        List<AlertRuleVo> list = alertRuleService.queryList(bo);
        ExcelUtil.exportExcel(list, "设备告警规则", AlertRuleVo.class, response);
    }

    /**
     * 获取设备告警规则详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("iot:alertRule:query")
    @GetMapping("/{id}")
    public R<AlertRuleVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(alertRuleService.queryById(id));
    }

    /**
     * 新增设备告警规则
     */
    @SaCheckPermission("iot:alertRule:add")
    @Log(title = "设备告警规则", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody AlertRuleBo bo) {
        return toAjax(alertRuleService.insertByBo(bo));
    }

    /**
     * 修改设备告警规则
     */
    @SaCheckPermission("iot:alertRule:edit")
    @Log(title = "设备告警规则", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody AlertRuleBo bo) {
        return toAjax(alertRuleService.updateByBo(bo));
    }

    /**
     * 删除设备告警规则
     *
     * @param ids 主键串
     */
    @SaCheckPermission("iot:alertRule:remove")
    @Log(title = "设备告警规则", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(alertRuleService.deleteWithValidByIds(List.of(ids), true));
    }
}
