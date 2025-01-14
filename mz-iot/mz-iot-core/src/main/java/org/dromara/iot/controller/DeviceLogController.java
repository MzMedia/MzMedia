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
import org.dromara.iot.domain.vo.DeviceLogVo;
import org.dromara.iot.domain.bo.DeviceLogBo;
import org.dromara.iot.service.IDeviceLogService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 设备日志
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/deviceLog")
public class DeviceLogController extends BaseController {

    private final IDeviceLogService deviceLogService;

    /**
     * 查询设备日志列表
     */
    @SaCheckPermission("iot:deviceLog:list")
    @GetMapping("/list")
    public TableDataInfo<DeviceLogVo> list(DeviceLogBo bo, PageQuery pageQuery) {
        return deviceLogService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出设备日志列表
     */
    @SaCheckPermission("iot:deviceLog:export")
    @Log(title = "设备日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DeviceLogBo bo, HttpServletResponse response) {
        List<DeviceLogVo> list = deviceLogService.queryList(bo);
        ExcelUtil.exportExcel(list, "设备日志", DeviceLogVo.class, response);
    }

    /**
     * 获取设备日志详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("iot:deviceLog:query")
    @GetMapping("/{id}")
    public R<DeviceLogVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(deviceLogService.queryById(id));
    }

    /**
     * 新增设备日志
     */
    @SaCheckPermission("iot:deviceLog:add")
    @Log(title = "设备日志", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DeviceLogBo bo) {
        return toAjax(deviceLogService.insertByBo(bo));
    }

    /**
     * 修改设备日志
     */
    @SaCheckPermission("iot:deviceLog:edit")
    @Log(title = "设备日志", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DeviceLogBo bo) {
        return toAjax(deviceLogService.updateByBo(bo));
    }

    /**
     * 删除设备日志
     *
     * @param ids 主键串
     */
    @SaCheckPermission("iot:deviceLog:remove")
    @Log(title = "设备日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(deviceLogService.deleteWithValidByIds(List.of(ids), true));
    }
}
