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
import org.dromara.iot.domain.vo.MediaServerVo;
import org.dromara.iot.domain.bo.MediaServerBo;
import org.dromara.iot.service.IMediaServerService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 流媒体服务器配置
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/mediaServer")
public class MediaServerController extends BaseController {

    private final IMediaServerService mediaServerService;

    /**
     * 查询流媒体服务器配置列表
     */
    @SaCheckPermission("iot:mediaServer:list")
    @GetMapping("/list")
    public TableDataInfo<MediaServerVo> list(MediaServerBo bo, PageQuery pageQuery) {
        return mediaServerService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出流媒体服务器配置列表
     */
    @SaCheckPermission("iot:mediaServer:export")
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
    @SaCheckPermission("iot:mediaServer:query")
    @GetMapping("/{id}")
    public R<MediaServerVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(mediaServerService.queryById(id));
    }

    /**
     * 新增流媒体服务器配置
     */
    @SaCheckPermission("iot:mediaServer:add")
    @Log(title = "流媒体服务器配置", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MediaServerBo bo) {
        return toAjax(mediaServerService.insertByBo(bo));
    }

    /**
     * 修改流媒体服务器配置
     */
    @SaCheckPermission("iot:mediaServer:edit")
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
    @SaCheckPermission("iot:mediaServer:remove")
    @Log(title = "流媒体服务器配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(mediaServerService.deleteWithValidByIds(List.of(ids), true));
    }
}
