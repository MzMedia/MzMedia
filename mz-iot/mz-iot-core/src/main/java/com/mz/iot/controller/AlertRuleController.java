package com.mz.iot.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.mz.common.idempotent.annotation.RepeatSubmit;
import com.mz.common.log.annotation.Log;
import com.mz.common.web.core.BaseController;
import com.mz.common.mybatis.core.page.PageQuery;
import com.mz.common.core.domain.R;
import com.mz.common.core.validate.AddGroup;
import com.mz.common.core.validate.EditGroup;
import com.mz.common.log.enums.BusinessType;
import com.mz.common.excel.utils.ExcelUtil;
import com.mz.iot.domain.vo.AlertRuleVo;
import com.mz.iot.domain.bo.AlertRuleBo;
import com.mz.iot.service.IAlertRuleService;
import com.mz.common.mybatis.core.page.TableDataInfo;

/**
 * 设备告警规则
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/Alertrule")
public class AlertRuleController extends BaseController {

    private final IAlertRuleService alertRuleService;

    /**
     * 查询设备告警规则列表
     */
    @SaCheckPermission("iot:rule:list")
    @GetMapping("/list")
    public TableDataInfo<AlertRuleVo> list(AlertRuleBo bo, PageQuery pageQuery) {
        return alertRuleService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出设备告警规则列表
     */
    @SaCheckPermission("iot:rule:export")
    @Log(title = "设备告警规则", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(AlertRuleBo bo, HttpServletResponse response) {
        List<AlertRuleVo> list = alertRuleService.queryList(bo);
        ExcelUtil.exportExcel(list, "设备告警规则", AlertRuleVo.class, response);
    }

    /**
     * 获取设备告警规则详细信息
     *
     * @param alertId 主键
     */
    @SaCheckPermission("iot:rule:query")
    @GetMapping("/{alertId}")
    public R<AlertRuleVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long alertId) {
        return R.ok(alertRuleService.queryById(alertId));
    }

    /**
     * 新增设备告警规则
     */
    @SaCheckPermission("iot:rule:add")
    @Log(title = "设备告警规则", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody AlertRuleBo bo) {
        return toAjax(alertRuleService.insertByBo(bo));
    }

    /**
     * 修改设备告警规则
     */
    @SaCheckPermission("iot:rule:edit")
    @Log(title = "设备告警规则", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody AlertRuleBo bo) {
        return toAjax(alertRuleService.updateByBo(bo));
    }

    /**
     * 删除设备告警规则
     *
     * @param alertIds 主键串
     */
    @SaCheckPermission("iot:rule:remove")
    @Log(title = "设备告警规则", businessType = BusinessType.DELETE)
    @DeleteMapping("/{alertIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] alertIds) {
        return toAjax(alertRuleService.deleteWithValidByIds(List.of(alertIds), true));
    }
}
