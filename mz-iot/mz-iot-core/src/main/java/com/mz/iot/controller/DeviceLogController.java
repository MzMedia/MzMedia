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
import com.mz.iot.domain.vo.DeviceLogVo;
import com.mz.iot.domain.bo.DeviceLogBo;
import com.mz.iot.service.IDeviceLogService;
import com.mz.common.mybatis.core.page.TableDataInfo;

/**
 * 设备日志
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/devicelog")
public class DeviceLogController extends BaseController {

    private final IDeviceLogService deviceLogService;

    /**
     * 查询设备日志列表
     */
    @SaCheckPermission("iot:log:list")
    @GetMapping("/list")
    public TableDataInfo<DeviceLogVo> list(DeviceLogBo bo, PageQuery pageQuery) {
        return deviceLogService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出设备日志列表
     */
    @SaCheckPermission("iot:log:export")
    @Log(title = "设备日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DeviceLogBo bo, HttpServletResponse response) {
        List<DeviceLogVo> list = deviceLogService.queryList(bo);
        ExcelUtil.exportExcel(list, "设备日志", DeviceLogVo.class, response);
    }

    /**
     * 获取设备日志详细信息
     *
     * @param logId 主键
     */
    @SaCheckPermission("iot:log:query")
    @GetMapping("/{logId}")
    public R<DeviceLogVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long logId) {
        return R.ok(deviceLogService.queryById(logId));
    }

    /**
     * 新增设备日志
     */
    @SaCheckPermission("iot:log:add")
    @Log(title = "设备日志", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DeviceLogBo bo) {
        return toAjax(deviceLogService.insertByBo(bo));
    }

    /**
     * 修改设备日志
     */
    @SaCheckPermission("iot:log:edit")
    @Log(title = "设备日志", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DeviceLogBo bo) {
        return toAjax(deviceLogService.updateByBo(bo));
    }

    /**
     * 删除设备日志
     *
     * @param logIds 主键串
     */
    @SaCheckPermission("iot:log:remove")
    @Log(title = "设备日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{logIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] logIds) {
        return toAjax(deviceLogService.deleteWithValidByIds(List.of(logIds), true));
    }
}
