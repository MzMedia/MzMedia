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
import org.dromara.iot.domain.vo.GroupVo;
import org.dromara.iot.domain.bo.GroupBo;
import org.dromara.iot.service.IGroupService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 设备分组
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/group")
public class GroupController extends BaseController {

    private final IGroupService groupService;

    /**
     * 查询设备分组列表
     */
    @SaCheckPermission("iot:group:list")
    @GetMapping("/list")
    public TableDataInfo<GroupVo> list(GroupBo bo, PageQuery pageQuery) {
        return groupService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出设备分组列表
     */
    @SaCheckPermission("iot:group:export")
    @Log(title = "设备分组", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(GroupBo bo, HttpServletResponse response) {
        List<GroupVo> list = groupService.queryList(bo);
        ExcelUtil.exportExcel(list, "设备分组", GroupVo.class, response);
    }

    /**
     * 获取设备分组详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("iot:group:query")
    @GetMapping("/{id}")
    public R<GroupVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(groupService.queryById(id));
    }

    /**
     * 新增设备分组
     */
    @SaCheckPermission("iot:group:add")
    @Log(title = "设备分组", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody GroupBo bo) {
        return toAjax(groupService.insertByBo(bo));
    }

    /**
     * 修改设备分组
     */
    @SaCheckPermission("iot:group:edit")
    @Log(title = "设备分组", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody GroupBo bo) {
        return toAjax(groupService.updateByBo(bo));
    }

    /**
     * 删除设备分组
     *
     * @param ids 主键串
     */
    @SaCheckPermission("iot:group:remove")
    @Log(title = "设备分组", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(groupService.deleteWithValidByIds(List.of(ids), true));
    }
}
