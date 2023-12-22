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
import com.mz.protocol.domain.vo.ProtocolCcdecVo;
import com.mz.protocol.domain.bo.ProtocolCcdecBo;
import com.mz.protocol.service.IProtocolCcdecService;
import com.mz.common.mybatis.core.page.TableDataInfo;

/**
 * 协议编解码器
 *
 * @author MzMedia
 * @date 2023-12-22
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/protocol/ccdec")
public class ProtocolCcdecController extends BaseController {

    private final IProtocolCcdecService protocolCcdecService;

    /**
     * 查询协议编解码器列表
     */
    @SaCheckPermission("protocol:ccdec:list")
    @GetMapping("/list")
    public TableDataInfo<ProtocolCcdecVo> list(ProtocolCcdecBo bo, PageQuery pageQuery) {
        return protocolCcdecService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出协议编解码器列表
     */
    @SaCheckPermission("protocol:ccdec:export")
    @Log(title = "协议编解码器", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ProtocolCcdecBo bo, HttpServletResponse response) {
        List<ProtocolCcdecVo> list = protocolCcdecService.queryList(bo);
        ExcelUtil.exportExcel(list, "协议编解码器", ProtocolCcdecVo.class, response);
    }

    /**
     * 获取协议编解码器详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("protocol:ccdec:query")
    @GetMapping("/{id}")
    public R<ProtocolCcdecVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(protocolCcdecService.queryById(id));
    }

    /**
     * 新增协议编解码器
     */
    @SaCheckPermission("protocol:ccdec:add")
    @Log(title = "协议编解码器", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ProtocolCcdecBo bo) {
        return toAjax(protocolCcdecService.insertByBo(bo));
    }

    /**
     * 修改协议编解码器
     */
    @SaCheckPermission("protocol:ccdec:edit")
    @Log(title = "协议编解码器", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ProtocolCcdecBo bo) {
        return toAjax(protocolCcdecService.updateByBo(bo));
    }

    /**
     * 删除协议编解码器
     *
     * @param ids 主键串
     */
    @SaCheckPermission("protocol:ccdec:remove")
    @Log(title = "协议编解码器", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(protocolCcdecService.deleteWithValidByIds(List.of(ids), true));
    }
}
