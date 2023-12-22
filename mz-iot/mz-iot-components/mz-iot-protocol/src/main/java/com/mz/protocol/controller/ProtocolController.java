package com.mz.protocol.controller;

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
import com.mz.protocol.domain.vo.ProtocolVo;
import com.mz.protocol.domain.bo.ProtocolBo;
import com.mz.protocol.service.IProtocolService;
import com.mz.common.mybatis.core.page.TableDataInfo;

/**
 * 协议
 *
 * @author MzMedia
 * @date 2023-12-22
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/protocol/protocol")
public class ProtocolController extends BaseController {

    private final IProtocolService protocolService;

    /**
     * 查询协议列表
     */
    @SaCheckPermission("protocol:protocol:list")
    @GetMapping("/list")
    public TableDataInfo<ProtocolVo> list(ProtocolBo bo, PageQuery pageQuery) {
        return protocolService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出协议列表
     */
    @SaCheckPermission("protocol:protocol:export")
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
    @SaCheckPermission("protocol:protocol:query")
    @GetMapping("/{id}")
    public R<ProtocolVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(protocolService.queryById(id));
    }

    /**
     * 新增协议
     */
    @SaCheckPermission("protocol:protocol:add")
    @Log(title = "协议", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ProtocolBo bo) {
        return toAjax(protocolService.insertByBo(bo));
    }

    /**
     * 修改协议
     */
    @SaCheckPermission("protocol:protocol:edit")
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
    @SaCheckPermission("protocol:protocol:remove")
    @Log(title = "协议", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(protocolService.deleteWithValidByIds(List.of(ids), true));
    }
}
