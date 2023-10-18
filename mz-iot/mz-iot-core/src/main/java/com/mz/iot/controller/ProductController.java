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
import com.mz.iot.domain.vo.ProductVo;
import com.mz.iot.domain.bo.ProductBo;
import com.mz.iot.service.IProductService;
import com.mz.common.mybatis.core.page.TableDataInfo;

/**
 * 产品
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/product")
public class ProductController extends BaseController {

    private final IProductService productService;

    /**
     * 查询产品列表
     */
    @SaCheckPermission("iot:product:list")
    @GetMapping("/list")
    public TableDataInfo<ProductVo> list(ProductBo bo, PageQuery pageQuery) {
        return productService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出产品列表
     */
    @SaCheckPermission("iot:product:export")
    @Log(title = "产品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ProductBo bo, HttpServletResponse response) {
        List<ProductVo> list = productService.queryList(bo);
        ExcelUtil.exportExcel(list, "产品", ProductVo.class, response);
    }

    /**
     * 获取产品详细信息
     *
     * @param productId 主键
     */
    @SaCheckPermission("iot:product:query")
    @GetMapping("/{productId}")
    public R<ProductVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long productId) {
        return R.ok(productService.queryById(productId));
    }

    /**
     * 新增产品
     */
    @SaCheckPermission("iot:product:add")
    @Log(title = "产品", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ProductBo bo) {
        return toAjax(productService.insertByBo(bo));
    }

    /**
     * 修改产品
     */
    @SaCheckPermission("iot:product:edit")
    @Log(title = "产品", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ProductBo bo) {
        return toAjax(productService.updateByBo(bo));
    }

    /**
     * 删除产品
     *
     * @param productIds 主键串
     */
    @SaCheckPermission("iot:product:remove")
    @Log(title = "产品", businessType = BusinessType.DELETE)
    @DeleteMapping("/{productIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] productIds) {
        return toAjax(productService.deleteWithValidByIds(List.of(productIds), true));
    }
}
