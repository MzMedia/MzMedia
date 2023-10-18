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
import com.mz.iot.domain.vo.SipDeviceChannelVo;
import com.mz.iot.domain.bo.SipDeviceChannelBo;
import com.mz.iot.service.ISipDeviceChannelService;
import com.mz.common.mybatis.core.page.TableDataInfo;

/**
 * 监控设备通道信息
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/deviceChannel")
public class SipDeviceChannelController extends BaseController {

    private final ISipDeviceChannelService sipDeviceChannelService;

    /**
     * 查询监控设备通道信息列表
     */
    @SaCheckPermission("iot:deviceChannel:list")
    @GetMapping("/list")
    public TableDataInfo<SipDeviceChannelVo> list(SipDeviceChannelBo bo, PageQuery pageQuery) {
        return sipDeviceChannelService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出监控设备通道信息列表
     */
    @SaCheckPermission("iot:deviceChannel:export")
    @Log(title = "监控设备通道信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SipDeviceChannelBo bo, HttpServletResponse response) {
        List<SipDeviceChannelVo> list = sipDeviceChannelService.queryList(bo);
        ExcelUtil.exportExcel(list, "监控设备通道信息", SipDeviceChannelVo.class, response);
    }

    /**
     * 获取监控设备通道信息详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("iot:deviceChannel:query")
    @GetMapping("/{id}")
    public R<SipDeviceChannelVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(sipDeviceChannelService.queryById(id));
    }

    /**
     * 新增监控设备通道信息
     */
    @SaCheckPermission("iot:deviceChannel:add")
    @Log(title = "监控设备通道信息", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SipDeviceChannelBo bo) {
        return toAjax(sipDeviceChannelService.insertByBo(bo));
    }

    /**
     * 修改监控设备通道信息
     */
    @SaCheckPermission("iot:deviceChannel:edit")
    @Log(title = "监控设备通道信息", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SipDeviceChannelBo bo) {
        return toAjax(sipDeviceChannelService.updateByBo(bo));
    }

    /**
     * 删除监控设备通道信息
     *
     * @param ids 主键串
     */
    @SaCheckPermission("iot:deviceChannel:remove")
    @Log(title = "监控设备通道信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(sipDeviceChannelService.deleteWithValidByIds(List.of(ids), true));
    }
}
