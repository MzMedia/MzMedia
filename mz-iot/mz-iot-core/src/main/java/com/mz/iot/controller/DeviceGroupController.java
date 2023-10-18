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
import com.mz.iot.domain.vo.DeviceGroupVo;
import com.mz.iot.domain.bo.DeviceGroupBo;
import com.mz.iot.service.IDeviceGroupService;
import com.mz.common.mybatis.core.page.TableDataInfo;

/**
 * 设备分组
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/group")
public class DeviceGroupController extends BaseController {

    private final IDeviceGroupService deviceGroupService;

    /**
     * 查询设备分组列表
     */
    @SaCheckPermission("iot:group:list")
    @GetMapping("/list")
    public TableDataInfo<DeviceGroupVo> list(DeviceGroupBo bo, PageQuery pageQuery) {
        return deviceGroupService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出设备分组列表
     */
    @SaCheckPermission("iot:group:export")
    @Log(title = "设备分组", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DeviceGroupBo bo, HttpServletResponse response) {
        List<DeviceGroupVo> list = deviceGroupService.queryList(bo);
        ExcelUtil.exportExcel(list, "设备分组", DeviceGroupVo.class, response);
    }

    /**
     * 获取设备分组详细信息
     *
     * @param deviceId 主键
     */
    @SaCheckPermission("iot:group:query")
    @GetMapping("/{deviceId}")
    public R<DeviceGroupVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long deviceId) {
        return R.ok(deviceGroupService.queryById(deviceId));
    }

    /**
     * 新增设备分组
     */
    @SaCheckPermission("iot:group:add")
    @Log(title = "设备分组", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DeviceGroupBo bo) {
        return toAjax(deviceGroupService.insertByBo(bo));
    }

    /**
     * 修改设备分组
     */
    @SaCheckPermission("iot:group:edit")
    @Log(title = "设备分组", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DeviceGroupBo bo) {
        return toAjax(deviceGroupService.updateByBo(bo));
    }

    /**
     * 删除设备分组
     *
     * @param deviceIds 主键串
     */
    @SaCheckPermission("iot:group:remove")
    @Log(title = "设备分组", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deviceIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] deviceIds) {
        return toAjax(deviceGroupService.deleteWithValidByIds(List.of(deviceIds), true));
    }
}
