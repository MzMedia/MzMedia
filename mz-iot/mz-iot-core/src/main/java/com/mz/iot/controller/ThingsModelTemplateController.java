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
import com.mz.iot.domain.vo.ThingsModelTemplateVo;
import com.mz.iot.domain.bo.ThingsModelTemplateBo;
import com.mz.iot.service.IThingsModelTemplateService;
import com.mz.common.mybatis.core.page.TableDataInfo;

/**
 * 物模型模板
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/modelTemplate")
public class ThingsModelTemplateController extends BaseController {

    private final IThingsModelTemplateService thingsModelTemplateService;

    /**
     * 查询物模型模板列表
     */
    @SaCheckPermission("iot:modelTemplate:list")
    @GetMapping("/list")
    public TableDataInfo<ThingsModelTemplateVo> list(ThingsModelTemplateBo bo, PageQuery pageQuery) {
        return thingsModelTemplateService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出物模型模板列表
     */
    @SaCheckPermission("iot:modelTemplate:export")
    @Log(title = "物模型模板", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ThingsModelTemplateBo bo, HttpServletResponse response) {
        List<ThingsModelTemplateVo> list = thingsModelTemplateService.queryList(bo);
        ExcelUtil.exportExcel(list, "物模型模板", ThingsModelTemplateVo.class, response);
    }

    /**
     * 获取物模型模板详细信息
     *
     * @param templateId 主键
     */
    @SaCheckPermission("iot:modelTemplate:query")
    @GetMapping("/{templateId}")
    public R<ThingsModelTemplateVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long templateId) {
        return R.ok(thingsModelTemplateService.queryById(templateId));
    }

    /**
     * 新增物模型模板
     */
    @SaCheckPermission("iot:modelTemplate:add")
    @Log(title = "物模型模板", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ThingsModelTemplateBo bo) {
        return toAjax(thingsModelTemplateService.insertByBo(bo));
    }

    /**
     * 修改物模型模板
     */
    @SaCheckPermission("iot:modelTemplate:edit")
    @Log(title = "物模型模板", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ThingsModelTemplateBo bo) {
        return toAjax(thingsModelTemplateService.updateByBo(bo));
    }

    /**
     * 删除物模型模板
     *
     * @param templateIds 主键串
     */
    @SaCheckPermission("iot:modelTemplate:remove")
    @Log(title = "物模型模板", businessType = BusinessType.DELETE)
    @DeleteMapping("/{templateIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] templateIds) {
        return toAjax(thingsModelTemplateService.deleteWithValidByIds(List.of(templateIds), true));
    }
}
