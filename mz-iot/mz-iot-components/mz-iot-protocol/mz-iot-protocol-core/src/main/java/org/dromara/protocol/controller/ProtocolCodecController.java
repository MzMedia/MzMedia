package org.dromara.protocol.controller;

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
import org.dromara.protocol.domain.vo.ProtocolCodecVo;
import org.dromara.protocol.domain.bo.ProtocolCodecBo;
import org.dromara.protocol.service.IProtocolCodecService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 协议编解码器
 *
 * @author MzMedia
 * @date 2023-12-25
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/protocol/codec")
public class ProtocolCodecController extends BaseController {

    private final IProtocolCodecService protocolCodecService;

    /**
     * 查询协议编解码器列表
     */
    @SaCheckPermission("protocol:codec:list")
    @GetMapping("/list")
    public TableDataInfo<ProtocolCodecVo> list(ProtocolCodecBo bo, PageQuery pageQuery) {
        return protocolCodecService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出协议编解码器列表
     */
    @SaCheckPermission("protocol:codec:export")
    @Log(title = "协议编解码器", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ProtocolCodecBo bo, HttpServletResponse response) {
        List<ProtocolCodecVo> list = protocolCodecService.queryList(bo);
        ExcelUtil.exportExcel(list, "协议编解码器", ProtocolCodecVo.class, response);
    }

    /**
     * 获取协议编解码器详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("protocol:codec:query")
    @GetMapping("/{id}")
    public R<ProtocolCodecVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(protocolCodecService.queryById(id));
    }

    /**
     * 新增协议编解码器
     */
    @SaCheckPermission("protocol:codec:add")
    @Log(title = "协议编解码器", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ProtocolCodecBo bo) {
        return toAjax(protocolCodecService.insertByBo(bo));
    }

    /**
     * 修改协议编解码器
     */
    @SaCheckPermission("protocol:codec:edit")
    @Log(title = "协议编解码器", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ProtocolCodecBo bo) {
        return toAjax(protocolCodecService.updateByBo(bo));
    }

    /**
     * 删除协议编解码器
     *
     * @param ids 主键串
     */
    @SaCheckPermission("protocol:codec:remove")
    @Log(title = "协议编解码器", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(protocolCodecService.deleteWithValidByIds(List.of(ids), true));
    }
}
