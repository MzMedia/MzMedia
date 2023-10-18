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
import com.mz.iot.domain.vo.GroupVo;
import com.mz.iot.domain.bo.GroupBo;
import com.mz.iot.service.IGroupService;
import com.mz.common.mybatis.core.page.TableDataInfo;

/**
 * 设备分组
 *
 * @author MzMedia
 * @date 2023-10-18
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
     * @param groupId 主键
     */
    @SaCheckPermission("iot:group:query")
    @GetMapping("/{groupId}")
    public R<GroupVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long groupId) {
        return R.ok(groupService.queryById(groupId));
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
     * @param groupIds 主键串
     */
    @SaCheckPermission("iot:group:remove")
    @Log(title = "设备分组", businessType = BusinessType.DELETE)
    @DeleteMapping("/{groupIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] groupIds) {
        return toAjax(groupService.deleteWithValidByIds(List.of(groupIds), true));
    }
}
