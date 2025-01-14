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
import org.dromara.iot.domain.vo.DeviceUserVo;
import org.dromara.iot.domain.bo.DeviceUserBo;
import org.dromara.iot.service.IDeviceUserService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 设备用户
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/deviceUser")
public class DeviceUserController extends BaseController {

    private final IDeviceUserService deviceUserService;

    /**
     * 查询设备用户列表
     */
    @SaCheckPermission("iot:deviceUser:list")
    @GetMapping("/list")
    public TableDataInfo<DeviceUserVo> list(DeviceUserBo bo, PageQuery pageQuery) {
        return deviceUserService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出设备用户列表
     */
    @SaCheckPermission("iot:deviceUser:export")
    @Log(title = "设备用户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DeviceUserBo bo, HttpServletResponse response) {
        List<DeviceUserVo> list = deviceUserService.queryList(bo);
        ExcelUtil.exportExcel(list, "设备用户", DeviceUserVo.class, response);
    }

    /**
     * 获取设备用户详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("iot:deviceUser:query")
    @GetMapping("/{id}")
    public R<DeviceUserVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(deviceUserService.queryById(id));
    }

    /**
     * 新增设备用户
     */
    @SaCheckPermission("iot:deviceUser:add")
    @Log(title = "设备用户", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DeviceUserBo bo) {
        return toAjax(deviceUserService.insertByBo(bo));
    }

    /**
     * 修改设备用户
     */
    @SaCheckPermission("iot:deviceUser:edit")
    @Log(title = "设备用户", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DeviceUserBo bo) {
        return toAjax(deviceUserService.updateByBo(bo));
    }

    /**
     * 删除设备用户
     *
     * @param ids 主键串
     */
    @SaCheckPermission("iot:deviceUser:remove")
    @Log(title = "设备用户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(deviceUserService.deleteWithValidByIds(List.of(ids), true));
    }
}
