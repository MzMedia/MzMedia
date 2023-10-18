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
import com.mz.iot.domain.vo.DeviceVo;
import com.mz.iot.domain.bo.DeviceBo;
import com.mz.iot.service.IDeviceService;
import com.mz.common.mybatis.core.page.TableDataInfo;

/**
 * 设备信息
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/device")
public class DeviceController extends BaseController {

    private final IDeviceService deviceService;

    /**
     * 查询设备信息列表
     */
    @SaCheckPermission("iot:device:list")
    @GetMapping("/list")
    public TableDataInfo<DeviceVo> list(DeviceBo bo, PageQuery pageQuery) {
        return deviceService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出设备信息列表
     */
    @SaCheckPermission("iot:device:export")
    @Log(title = "设备信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DeviceBo bo, HttpServletResponse response) {
        List<DeviceVo> list = deviceService.queryList(bo);
        ExcelUtil.exportExcel(list, "设备信息", DeviceVo.class, response);
    }

    /**
     * 获取设备信息详细信息
     *
     * @param userId 主键
     */
    @SaCheckPermission("iot:device:query")
    @GetMapping("/{userId}")
    public R<DeviceVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long userId) {
        return R.ok(deviceService.queryById(userId));
    }

    /**
     * 新增设备信息
     */
    @SaCheckPermission("iot:device:add")
    @Log(title = "设备信息", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DeviceBo bo) {
        return toAjax(deviceService.insertByBo(bo));
    }

    /**
     * 修改设备信息
     */
    @SaCheckPermission("iot:device:edit")
    @Log(title = "设备信息", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DeviceBo bo) {
        return toAjax(deviceService.updateByBo(bo));
    }

    /**
     * 删除设备信息
     *
     * @param userIds 主键串
     */
    @SaCheckPermission("iot:device:remove")
    @Log(title = "设备信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] userIds) {
        return toAjax(deviceService.deleteWithValidByIds(List.of(userIds), true));
    }
}
