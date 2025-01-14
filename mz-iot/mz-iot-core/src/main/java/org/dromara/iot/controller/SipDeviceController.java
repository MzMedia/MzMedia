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
import org.dromara.iot.domain.vo.SipDeviceVo;
import org.dromara.iot.domain.bo.SipDeviceBo;
import org.dromara.iot.service.ISipDeviceService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 监控设备
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/sipDevice")
public class SipDeviceController extends BaseController {

    private final ISipDeviceService sipDeviceService;

    /**
     * 查询监控设备列表
     */
    @SaCheckPermission("iot:sipDevice:list")
    @GetMapping("/list")
    public TableDataInfo<SipDeviceVo> list(SipDeviceBo bo, PageQuery pageQuery) {
        return sipDeviceService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出监控设备列表
     */
    @SaCheckPermission("iot:sipDevice:export")
    @Log(title = "监控设备", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SipDeviceBo bo, HttpServletResponse response) {
        List<SipDeviceVo> list = sipDeviceService.queryList(bo);
        ExcelUtil.exportExcel(list, "监控设备", SipDeviceVo.class, response);
    }

    /**
     * 获取监控设备详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("iot:sipDevice:query")
    @GetMapping("/{id}")
    public R<SipDeviceVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(sipDeviceService.queryById(id));
    }

    /**
     * 新增监控设备
     */
    @SaCheckPermission("iot:sipDevice:add")
    @Log(title = "监控设备", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SipDeviceBo bo) {
        return toAjax(sipDeviceService.insertByBo(bo));
    }

    /**
     * 修改监控设备
     */
    @SaCheckPermission("iot:sipDevice:edit")
    @Log(title = "监控设备", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SipDeviceBo bo) {
        return toAjax(sipDeviceService.updateByBo(bo));
    }

    /**
     * 删除监控设备
     *
     * @param ids 主键串
     */
    @SaCheckPermission("iot:sipDevice:remove")
    @Log(title = "监控设备", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(sipDeviceService.deleteWithValidByIds(List.of(ids), true));
    }
}
