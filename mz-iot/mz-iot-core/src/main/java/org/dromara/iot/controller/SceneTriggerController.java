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
import org.dromara.iot.domain.vo.SceneTriggerVo;
import org.dromara.iot.domain.bo.SceneTriggerBo;
import org.dromara.iot.service.ISceneTriggerService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 场景联动触发器
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/sceneTrigger")
public class SceneTriggerController extends BaseController {

    private final ISceneTriggerService sceneTriggerService;

    /**
     * 查询场景联动触发器列表
     */
    @SaCheckPermission("iot:sceneTrigger:list")
    @GetMapping("/list")
    public TableDataInfo<SceneTriggerVo> list(SceneTriggerBo bo, PageQuery pageQuery) {
        return sceneTriggerService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出场景联动触发器列表
     */
    @SaCheckPermission("iot:sceneTrigger:export")
    @Log(title = "场景联动触发器", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SceneTriggerBo bo, HttpServletResponse response) {
        List<SceneTriggerVo> list = sceneTriggerService.queryList(bo);
        ExcelUtil.exportExcel(list, "场景联动触发器", SceneTriggerVo.class, response);
    }

    /**
     * 获取场景联动触发器详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("iot:sceneTrigger:query")
    @GetMapping("/{id}")
    public R<SceneTriggerVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(sceneTriggerService.queryById(id));
    }

    /**
     * 新增场景联动触发器
     */
    @SaCheckPermission("iot:sceneTrigger:add")
    @Log(title = "场景联动触发器", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SceneTriggerBo bo) {
        return toAjax(sceneTriggerService.insertByBo(bo));
    }

    /**
     * 修改场景联动触发器
     */
    @SaCheckPermission("iot:sceneTrigger:edit")
    @Log(title = "场景联动触发器", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SceneTriggerBo bo) {
        return toAjax(sceneTriggerService.updateByBo(bo));
    }

    /**
     * 删除场景联动触发器
     *
     * @param ids 主键串
     */
    @SaCheckPermission("iot:sceneTrigger:remove")
    @Log(title = "场景联动触发器", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(sceneTriggerService.deleteWithValidByIds(List.of(ids), true));
    }
}
