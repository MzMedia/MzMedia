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
import com.mz.iot.domain.vo.DeviceUserVo;
import com.mz.iot.domain.bo.DeviceUserBo;
import com.mz.iot.service.IDeviceUserService;
import com.mz.common.mybatis.core.page.TableDataInfo;

/**
 * 设备用户
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/user")
public class DeviceUserController extends BaseController {

    private final IDeviceUserService deviceUserService;

    /**
     * 查询设备用户列表
     */
    @SaCheckPermission("iot:user:list")
    @GetMapping("/list")
    public TableDataInfo<DeviceUserVo> list(DeviceUserBo bo, PageQuery pageQuery) {
        return deviceUserService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出设备用户列表
     */
    @SaCheckPermission("iot:user:export")
    @Log(title = "设备用户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DeviceUserBo bo, HttpServletResponse response) {
        List<DeviceUserVo> list = deviceUserService.queryList(bo);
        ExcelUtil.exportExcel(list, "设备用户", DeviceUserVo.class, response);
    }

    /**
     * 获取设备用户详细信息
     *
     * @param userId 主键
     */
    @SaCheckPermission("iot:user:query")
    @GetMapping("/{userId}")
    public R<DeviceUserVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long userId) {
        return R.ok(deviceUserService.queryById(userId));
    }

    /**
     * 新增设备用户
     */
    @SaCheckPermission("iot:user:add")
    @Log(title = "设备用户", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DeviceUserBo bo) {
        return toAjax(deviceUserService.insertByBo(bo));
    }

    /**
     * 修改设备用户
     */
    @SaCheckPermission("iot:user:edit")
    @Log(title = "设备用户", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DeviceUserBo bo) {
        return toAjax(deviceUserService.updateByBo(bo));
    }

    /**
     * 删除设备用户
     *
     * @param userIds 主键串
     */
    @SaCheckPermission("iot:user:remove")
    @Log(title = "设备用户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] userIds) {
        return toAjax(deviceUserService.deleteWithValidByIds(List.of(userIds), true));
    }
}
