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
import org.dromara.iot.domain.vo.ThingsModelVo;
import org.dromara.iot.domain.bo.ThingsModelBo;
import org.dromara.iot.service.IThingsModelService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 物模型
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/thingsModel")
public class ThingsModelController extends BaseController {

    private final IThingsModelService thingsModelService;

    /**
     * 查询物模型列表
     */
    @SaCheckPermission("iot:thingsModel:list")
    @GetMapping("/list")
    public TableDataInfo<ThingsModelVo> list(ThingsModelBo bo, PageQuery pageQuery) {
        return thingsModelService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出物模型列表
     */
    @SaCheckPermission("iot:thingsModel:export")
    @Log(title = "物模型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ThingsModelBo bo, HttpServletResponse response) {
        List<ThingsModelVo> list = thingsModelService.queryList(bo);
        ExcelUtil.exportExcel(list, "物模型", ThingsModelVo.class, response);
    }

    /**
     * 获取物模型详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("iot:thingsModel:query")
    @GetMapping("/{id}")
    public R<ThingsModelVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(thingsModelService.queryById(id));
    }

    /**
     * 新增物模型
     */
    @SaCheckPermission("iot:thingsModel:add")
    @Log(title = "物模型", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ThingsModelBo bo) {
        return toAjax(thingsModelService.insertByBo(bo));
    }

    /**
     * 修改物模型
     */
    @SaCheckPermission("iot:thingsModel:edit")
    @Log(title = "物模型", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ThingsModelBo bo) {
        return toAjax(thingsModelService.updateByBo(bo));
    }

    /**
     * 删除物模型
     *
     * @param ids 主键串
     */
    @SaCheckPermission("iot:thingsModel:remove")
    @Log(title = "物模型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(thingsModelService.deleteWithValidByIds(List.of(ids), true));
    }
}
