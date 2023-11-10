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
import com.mz.iot.domain.vo.SipDeviceVo;
import com.mz.iot.domain.bo.SipDeviceBo;
import com.mz.iot.service.ISipDeviceService;
import com.mz.common.mybatis.core.page.TableDataInfo;

/**
 * 监控设备
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/sipdevice")
public class SipDeviceController extends BaseController {

    private final ISipDeviceService sipDeviceService;

    /**
     * 查询监控设备列表
     */
    @SaCheckPermission("iot:device:list")
    @GetMapping("/list")
    public TableDataInfo<SipDeviceVo> list(SipDeviceBo bo, PageQuery pageQuery) {
        return sipDeviceService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出监控设备列表
     */
    @SaCheckPermission("iot:device:export")
    @Log(title = "监控设备", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SipDeviceBo bo, HttpServletResponse response) {
        List<SipDeviceVo> list = sipDeviceService.queryList(bo);
        ExcelUtil.exportExcel(list, "监控设备", SipDeviceVo.class, response);
    }

    /**
     * 获取监控设备详细信息
     *
     * @param deviceId 主键
     */
    @SaCheckPermission("iot:device:query")
    @GetMapping("/{deviceId}")
    public R<SipDeviceVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long deviceId) {
        return R.ok(sipDeviceService.queryById(deviceId));
    }

    /**
     * 新增监控设备
     */
    @SaCheckPermission("iot:device:add")
    @Log(title = "监控设备", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SipDeviceBo bo) {
        return toAjax(sipDeviceService.insertByBo(bo));
    }

    /**
     * 修改监控设备
     */
    @SaCheckPermission("iot:device:edit")
    @Log(title = "监控设备", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SipDeviceBo bo) {
        return toAjax(sipDeviceService.updateByBo(bo));
    }

    /**
     * 删除监控设备
     *
     * @param deviceIds 主键串
     */
    @SaCheckPermission("iot:device:remove")
    @Log(title = "监控设备", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deviceIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] deviceIds) {
        return toAjax(sipDeviceService.deleteWithValidByIds(List.of(deviceIds), true));
    }
}
