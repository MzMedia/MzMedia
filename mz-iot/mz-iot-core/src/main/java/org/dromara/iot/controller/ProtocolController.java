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
import org.dromara.iot.domain.vo.ProtocolVo;
import org.dromara.iot.domain.bo.ProtocolBo;
import org.dromara.iot.service.IProtocolService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 协议
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/protocol")
public class ProtocolController extends BaseController {

    private final IProtocolService protocolService;

    /**
     * 查询协议列表
     */
    @SaCheckPermission("iot:protocol:list")
    @GetMapping("/list")
    public TableDataInfo<ProtocolVo> list(ProtocolBo bo, PageQuery pageQuery) {
        return protocolService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出协议列表
     */
    @SaCheckPermission("iot:protocol:export")
    @Log(title = "协议", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ProtocolBo bo, HttpServletResponse response) {
        List<ProtocolVo> list = protocolService.queryList(bo);
        ExcelUtil.exportExcel(list, "协议", ProtocolVo.class, response);
    }

    /**
     * 获取协议详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("iot:protocol:query")
    @GetMapping("/{id}")
    public R<ProtocolVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(protocolService.queryById(id));
    }

    /**
     * 新增协议
     */
    @SaCheckPermission("iot:protocol:add")
    @Log(title = "协议", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ProtocolBo bo) {
        return toAjax(protocolService.insertByBo(bo));
    }

    /**
     * 修改协议
     */
    @SaCheckPermission("iot:protocol:edit")
    @Log(title = "协议", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ProtocolBo bo) {
        return toAjax(protocolService.updateByBo(bo));
    }

    /**
     * 删除协议
     *
     * @param ids 主键串
     */
    @SaCheckPermission("iot:protocol:remove")
    @Log(title = "协议", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(protocolService.deleteWithValidByIds(List.of(ids), true));
    }
}
