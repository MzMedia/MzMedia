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
import com.mz.iot.domain.vo.AlertLogVo;
import com.mz.iot.domain.bo.AlertLogBo;
import com.mz.iot.service.IAlertLogService;
import com.mz.common.mybatis.core.page.TableDataInfo;

/**
 * 设备告警日志
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/log")
public class AlertLogController extends BaseController {

    private final IAlertLogService alertLogService;

    /**
     * 查询设备告警日志列表
     */
    @SaCheckPermission("iot:log:list")
    @GetMapping("/list")
    public TableDataInfo<AlertLogVo> list(AlertLogBo bo, PageQuery pageQuery) {
        return alertLogService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出设备告警日志列表
     */
    @SaCheckPermission("iot:log:export")
    @Log(title = "设备告警日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(AlertLogBo bo, HttpServletResponse response) {
        List<AlertLogVo> list = alertLogService.queryList(bo);
        ExcelUtil.exportExcel(list, "设备告警日志", AlertLogVo.class, response);
    }

    /**
     * 获取设备告警日志详细信息
     *
     * @param alertLogId 主键
     */
    @SaCheckPermission("iot:log:query")
    @GetMapping("/{alertLogId}")
    public R<AlertLogVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long alertLogId) {
        return R.ok(alertLogService.queryById(alertLogId));
    }

    /**
     * 新增设备告警日志
     */
    @SaCheckPermission("iot:log:add")
    @Log(title = "设备告警日志", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody AlertLogBo bo) {
        return toAjax(alertLogService.insertByBo(bo));
    }

    /**
     * 修改设备告警日志
     */
    @SaCheckPermission("iot:log:edit")
    @Log(title = "设备告警日志", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody AlertLogBo bo) {
        return toAjax(alertLogService.updateByBo(bo));
    }

    /**
     * 删除设备告警日志
     *
     * @param alertLogIds 主键串
     */
    @SaCheckPermission("iot:log:remove")
    @Log(title = "设备告警日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{alertLogIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] alertLogIds) {
        return toAjax(alertLogService.deleteWithValidByIds(List.of(alertLogIds), true));
    }
}
