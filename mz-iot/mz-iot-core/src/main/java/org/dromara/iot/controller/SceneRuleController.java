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
import org.dromara.iot.domain.vo.SceneRuleVo;
import org.dromara.iot.domain.bo.SceneRuleBo;
import org.dromara.iot.service.ISceneRuleService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 场景联动
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/sceneRule")
public class SceneRuleController extends BaseController {

    private final ISceneRuleService sceneRuleService;

    /**
     * 查询场景联动列表
     */
    @SaCheckPermission("iot:sceneRule:list")
    @GetMapping("/list")
    public TableDataInfo<SceneRuleVo> list(SceneRuleBo bo, PageQuery pageQuery) {
        return sceneRuleService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出场景联动列表
     */
    @SaCheckPermission("iot:sceneRule:export")
    @Log(title = "场景联动", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SceneRuleBo bo, HttpServletResponse response) {
        List<SceneRuleVo> list = sceneRuleService.queryList(bo);
        ExcelUtil.exportExcel(list, "场景联动", SceneRuleVo.class, response);
    }

    /**
     * 获取场景联动详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("iot:sceneRule:query")
    @GetMapping("/{id}")
    public R<SceneRuleVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(sceneRuleService.queryById(id));
    }

    /**
     * 新增场景联动
     */
    @SaCheckPermission("iot:sceneRule:add")
    @Log(title = "场景联动", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SceneRuleBo bo) {
        return toAjax(sceneRuleService.insertByBo(bo));
    }

    /**
     * 修改场景联动
     */
    @SaCheckPermission("iot:sceneRule:edit")
    @Log(title = "场景联动", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SceneRuleBo bo) {
        return toAjax(sceneRuleService.updateByBo(bo));
    }

    /**
     * 删除场景联动
     *
     * @param ids 主键串
     */
    @SaCheckPermission("iot:sceneRule:remove")
    @Log(title = "场景联动", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(sceneRuleService.deleteWithValidByIds(List.of(ids), true));
    }
}
