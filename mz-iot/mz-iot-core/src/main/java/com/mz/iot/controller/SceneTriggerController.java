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
import com.mz.iot.domain.vo.SceneTriggerVo;
import com.mz.iot.domain.bo.SceneTriggerBo;
import com.mz.iot.service.ISceneTriggerService;
import com.mz.common.mybatis.core.page.TableDataInfo;

/**
 * 场景联动触发器
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/trigger")
public class SceneTriggerController extends BaseController {

    private final ISceneTriggerService sceneTriggerService;

    /**
     * 查询场景联动触发器列表
     */
    @SaCheckPermission("iot:trigger:list")
    @GetMapping("/list")
    public TableDataInfo<SceneTriggerVo> list(SceneTriggerBo bo, PageQuery pageQuery) {
        return sceneTriggerService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出场景联动触发器列表
     */
    @SaCheckPermission("iot:trigger:export")
    @Log(title = "场景联动触发器", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SceneTriggerBo bo, HttpServletResponse response) {
        List<SceneTriggerVo> list = sceneTriggerService.queryList(bo);
        ExcelUtil.exportExcel(list, "场景联动触发器", SceneTriggerVo.class, response);
    }

    /**
     * 获取场景联动触发器详细信息
     *
     * @param sceneTriggerId 主键
     */
    @SaCheckPermission("iot:trigger:query")
    @GetMapping("/{sceneTriggerId}")
    public R<SceneTriggerVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long sceneTriggerId) {
        return R.ok(sceneTriggerService.queryById(sceneTriggerId));
    }

    /**
     * 新增场景联动触发器
     */
    @SaCheckPermission("iot:trigger:add")
    @Log(title = "场景联动触发器", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SceneTriggerBo bo) {
        return toAjax(sceneTriggerService.insertByBo(bo));
    }

    /**
     * 修改场景联动触发器
     */
    @SaCheckPermission("iot:trigger:edit")
    @Log(title = "场景联动触发器", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SceneTriggerBo bo) {
        return toAjax(sceneTriggerService.updateByBo(bo));
    }

    /**
     * 删除场景联动触发器
     *
     * @param sceneTriggerIds 主键串
     */
    @SaCheckPermission("iot:trigger:remove")
    @Log(title = "场景联动触发器", businessType = BusinessType.DELETE)
    @DeleteMapping("/{sceneTriggerIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] sceneTriggerIds) {
        return toAjax(sceneTriggerService.deleteWithValidByIds(List.of(sceneTriggerIds), true));
    }
}
