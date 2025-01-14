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
import org.dromara.iot.domain.vo.AlertLogVo;
import org.dromara.iot.domain.bo.AlertLogBo;
import org.dromara.iot.service.IAlertLogService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 设备告警日志
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/alertLog")
public class AlertLogController extends BaseController {

    private final IAlertLogService alertLogService;

    /**
     * 查询设备告警日志列表
     */
    @SaCheckPermission("iot:alertLog:list")
    @GetMapping("/list")
    public TableDataInfo<AlertLogVo> list(AlertLogBo bo, PageQuery pageQuery) {
        return alertLogService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出设备告警日志列表
     */
    @SaCheckPermission("iot:alertLog:export")
    @Log(title = "设备告警日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(AlertLogBo bo, HttpServletResponse response) {
        List<AlertLogVo> list = alertLogService.queryList(bo);
        ExcelUtil.exportExcel(list, "设备告警日志", AlertLogVo.class, response);
    }

    /**
     * 获取设备告警日志详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("iot:alertLog:query")
    @GetMapping("/{id}")
    public R<AlertLogVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(alertLogService.queryById(id));
    }

    /**
     * 新增设备告警日志
     */
    @SaCheckPermission("iot:alertLog:add")
    @Log(title = "设备告警日志", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody AlertLogBo bo) {
        return toAjax(alertLogService.insertByBo(bo));
    }

    /**
     * 修改设备告警日志
     */
    @SaCheckPermission("iot:alertLog:edit")
    @Log(title = "设备告警日志", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody AlertLogBo bo) {
        return toAjax(alertLogService.updateByBo(bo));
    }

    /**
     * 删除设备告警日志
     *
     * @param ids 主键串
     */
    @SaCheckPermission("iot:alertLog:remove")
    @Log(title = "设备告警日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(alertLogService.deleteWithValidByIds(List.of(ids), true));
    }
}
