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
import com.mz.iot.domain.vo.ThingsModelVo;
import com.mz.iot.domain.bo.ThingsModelBo;
import com.mz.iot.service.IThingsModelService;
import com.mz.common.mybatis.core.page.TableDataInfo;

/**
 * 物模型
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/model")
public class ThingsModelController extends BaseController {

    private final IThingsModelService thingsModelService;

    /**
     * 查询物模型列表
     */
    @SaCheckPermission("iot:model:list")
    @GetMapping("/list")
    public TableDataInfo<ThingsModelVo> list(ThingsModelBo bo, PageQuery pageQuery) {
        return thingsModelService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出物模型列表
     */
    @SaCheckPermission("iot:model:export")
    @Log(title = "物模型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ThingsModelBo bo, HttpServletResponse response) {
        List<ThingsModelVo> list = thingsModelService.queryList(bo);
        ExcelUtil.exportExcel(list, "物模型", ThingsModelVo.class, response);
    }

    /**
     * 获取物模型详细信息
     *
     * @param modelId 主键
     */
    @SaCheckPermission("iot:model:query")
    @GetMapping("/{modelId}")
    public R<ThingsModelVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long modelId) {
        return R.ok(thingsModelService.queryById(modelId));
    }

    /**
     * 新增物模型
     */
    @SaCheckPermission("iot:model:add")
    @Log(title = "物模型", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ThingsModelBo bo) {
        return toAjax(thingsModelService.insertByBo(bo));
    }

    /**
     * 修改物模型
     */
    @SaCheckPermission("iot:model:edit")
    @Log(title = "物模型", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ThingsModelBo bo) {
        return toAjax(thingsModelService.updateByBo(bo));
    }

    /**
     * 删除物模型
     *
     * @param modelIds 主键串
     */
    @SaCheckPermission("iot:model:remove")
    @Log(title = "物模型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{modelIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] modelIds) {
        return toAjax(thingsModelService.deleteWithValidByIds(List.of(modelIds), true));
    }
}
