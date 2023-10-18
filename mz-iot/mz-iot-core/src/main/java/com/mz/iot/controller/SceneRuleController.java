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
import com.mz.iot.domain.vo.SceneRuleVo;
import com.mz.iot.domain.bo.SceneRuleBo;
import com.mz.iot.service.ISceneRuleService;
import com.mz.common.mybatis.core.page.TableDataInfo;

/**
 * 场景联动
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/rule")
public class SceneRuleController extends BaseController {

    private final ISceneRuleService sceneRuleService;

    /**
     * 查询场景联动列表
     */
    @SaCheckPermission("iot:rule:list")
    @GetMapping("/list")
    public TableDataInfo<SceneRuleVo> list(SceneRuleBo bo, PageQuery pageQuery) {
        return sceneRuleService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出场景联动列表
     */
    @SaCheckPermission("iot:rule:export")
    @Log(title = "场景联动", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SceneRuleBo bo, HttpServletResponse response) {
        List<SceneRuleVo> list = sceneRuleService.queryList(bo);
        ExcelUtil.exportExcel(list, "场景联动", SceneRuleVo.class, response);
    }

    /**
     * 获取场景联动详细信息
     *
     * @param sceneId 主键
     */
    @SaCheckPermission("iot:rule:query")
    @GetMapping("/{sceneId}")
    public R<SceneRuleVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long sceneId) {
        return R.ok(sceneRuleService.queryById(sceneId));
    }

    /**
     * 新增场景联动
     */
    @SaCheckPermission("iot:rule:add")
    @Log(title = "场景联动", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SceneRuleBo bo) {
        return toAjax(sceneRuleService.insertByBo(bo));
    }

    /**
     * 修改场景联动
     */
    @SaCheckPermission("iot:rule:edit")
    @Log(title = "场景联动", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SceneRuleBo bo) {
        return toAjax(sceneRuleService.updateByBo(bo));
    }

    /**
     * 删除场景联动
     *
     * @param sceneIds 主键串
     */
    @SaCheckPermission("iot:rule:remove")
    @Log(title = "场景联动", businessType = BusinessType.DELETE)
    @DeleteMapping("/{sceneIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] sceneIds) {
        return toAjax(sceneRuleService.deleteWithValidByIds(List.of(sceneIds), true));
    }
}
