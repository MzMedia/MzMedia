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
import com.mz.iot.domain.vo.MediaServerVo;
import com.mz.iot.domain.bo.MediaServerBo;
import com.mz.iot.service.IMediaServerService;
import com.mz.common.mybatis.core.page.TableDataInfo;

/**
 * 流媒体服务器配置
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/server")
public class MediaServerController extends BaseController {

    private final IMediaServerService mediaServerService;

    /**
     * 查询流媒体服务器配置列表
     */
    @SaCheckPermission("iot:server:list")
    @GetMapping("/list")
    public TableDataInfo<MediaServerVo> list(MediaServerBo bo, PageQuery pageQuery) {
        return mediaServerService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出流媒体服务器配置列表
     */
    @SaCheckPermission("iot:server:export")
    @Log(title = "流媒体服务器配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MediaServerBo bo, HttpServletResponse response) {
        List<MediaServerVo> list = mediaServerService.queryList(bo);
        ExcelUtil.exportExcel(list, "流媒体服务器配置", MediaServerVo.class, response);
    }

    /**
     * 获取流媒体服务器配置详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("iot:server:query")
    @GetMapping("/{id}")
    public R<MediaServerVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(mediaServerService.queryById(id));
    }

    /**
     * 新增流媒体服务器配置
     */
    @SaCheckPermission("iot:server:add")
    @Log(title = "流媒体服务器配置", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MediaServerBo bo) {
        return toAjax(mediaServerService.insertByBo(bo));
    }

    /**
     * 修改流媒体服务器配置
     */
    @SaCheckPermission("iot:server:edit")
    @Log(title = "流媒体服务器配置", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MediaServerBo bo) {
        return toAjax(mediaServerService.updateByBo(bo));
    }

    /**
     * 删除流媒体服务器配置
     *
     * @param ids 主键串
     */
    @SaCheckPermission("iot:server:remove")
    @Log(title = "流媒体服务器配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(mediaServerService.deleteWithValidByIds(List.of(ids), true));
    }
}
