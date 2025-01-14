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
import org.dromara.iot.domain.vo.SipConfigVo;
import org.dromara.iot.domain.bo.SipConfigBo;
import org.dromara.iot.service.ISipConfigService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * sip系统配置
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/sipConfig")
public class SipConfigController extends BaseController {

    private final ISipConfigService sipConfigService;

    /**
     * 查询sip系统配置列表
     */
    @SaCheckPermission("iot:sipConfig:list")
    @GetMapping("/list")
    public TableDataInfo<SipConfigVo> list(SipConfigBo bo, PageQuery pageQuery) {
        return sipConfigService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出sip系统配置列表
     */
    @SaCheckPermission("iot:sipConfig:export")
    @Log(title = "sip系统配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SipConfigBo bo, HttpServletResponse response) {
        List<SipConfigVo> list = sipConfigService.queryList(bo);
        ExcelUtil.exportExcel(list, "sip系统配置", SipConfigVo.class, response);
    }

    /**
     * 获取sip系统配置详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("iot:sipConfig:query")
    @GetMapping("/{id}")
    public R<SipConfigVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(sipConfigService.queryById(id));
    }

    /**
     * 新增sip系统配置
     */
    @SaCheckPermission("iot:sipConfig:add")
    @Log(title = "sip系统配置", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SipConfigBo bo) {
        return toAjax(sipConfigService.insertByBo(bo));
    }

    /**
     * 修改sip系统配置
     */
    @SaCheckPermission("iot:sipConfig:edit")
    @Log(title = "sip系统配置", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SipConfigBo bo) {
        return toAjax(sipConfigService.updateByBo(bo));
    }

    /**
     * 删除sip系统配置
     *
     * @param ids 主键串
     */
    @SaCheckPermission("iot:sipConfig:remove")
    @Log(title = "sip系统配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(sipConfigService.deleteWithValidByIds(List.of(ids), true));
    }
}
