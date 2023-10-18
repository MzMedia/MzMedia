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
import com.mz.iot.domain.vo.CategoryVo;
import com.mz.iot.domain.bo.CategoryBo;
import com.mz.iot.service.ICategoryService;
import com.mz.common.mybatis.core.page.TableDataInfo;

/**
 * 产品分类
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/category")
public class CategoryController extends BaseController {

    private final ICategoryService categoryService;

    /**
     * 查询产品分类列表
     */
    @SaCheckPermission("iot:category:list")
    @GetMapping("/list")
    public TableDataInfo<CategoryVo> list(CategoryBo bo, PageQuery pageQuery) {
        return categoryService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出产品分类列表
     */
    @SaCheckPermission("iot:category:export")
    @Log(title = "产品分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(CategoryBo bo, HttpServletResponse response) {
        List<CategoryVo> list = categoryService.queryList(bo);
        ExcelUtil.exportExcel(list, "产品分类", CategoryVo.class, response);
    }

    /**
     * 获取产品分类详细信息
     *
     * @param categoryId 主键
     */
    @SaCheckPermission("iot:category:query")
    @GetMapping("/{categoryId}")
    public R<CategoryVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long categoryId) {
        return R.ok(categoryService.queryById(categoryId));
    }

    /**
     * 新增产品分类
     */
    @SaCheckPermission("iot:category:add")
    @Log(title = "产品分类", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody CategoryBo bo) {
        return toAjax(categoryService.insertByBo(bo));
    }

    /**
     * 修改产品分类
     */
    @SaCheckPermission("iot:category:edit")
    @Log(title = "产品分类", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody CategoryBo bo) {
        return toAjax(categoryService.updateByBo(bo));
    }

    /**
     * 删除产品分类
     *
     * @param categoryIds 主键串
     */
    @SaCheckPermission("iot:category:remove")
    @Log(title = "产品分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{categoryIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] categoryIds) {
        return toAjax(categoryService.deleteWithValidByIds(List.of(categoryIds), true));
    }
}
