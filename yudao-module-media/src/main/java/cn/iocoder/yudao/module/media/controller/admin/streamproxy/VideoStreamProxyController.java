package cn.iocoder.yudao.module.media.controller.admin.streamproxy;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.media.controller.admin.streamproxy.vo.VideoStreamProxyPageReqVO;
import cn.iocoder.yudao.module.media.controller.admin.streamproxy.vo.VideoStreamProxyRespVO;
import cn.iocoder.yudao.module.media.controller.admin.streamproxy.vo.VideoStreamProxySaveReqVO;
import cn.iocoder.yudao.module.media.convert.VideoStreamProxyConvert;
import cn.iocoder.yudao.module.media.dal.dataobject.stream.VideoStreamProxyDO;
import cn.iocoder.yudao.module.media.service.streamproxy.VideoStreamProxyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 视频拉流代理")
@RestController
@RequestMapping("/video/stream-proxy")
@Validated
public class VideoStreamProxyController {

    @Resource
    private VideoStreamProxyService videoStreamProxyService;

    @PostMapping("/create")
    @Operation(summary = "创建拉流代理")
    @PreAuthorize("@ss.hasPermission('video:stream-proxy:create')")
    public CommonResult<Long> createStreamProxy(@Valid @RequestBody VideoStreamProxySaveReqVO reqVO) {
        return success(videoStreamProxyService.createStreamProxy(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新拉流代理")
    @PreAuthorize("@ss.hasPermission('video:stream-proxy:update')")
    public CommonResult<Boolean> updateStreamProxy(@Valid @RequestBody VideoStreamProxySaveReqVO reqVO) {
        videoStreamProxyService.updateStreamProxy(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除拉流代理")
    @PreAuthorize("@ss.hasPermission('video:stream-proxy:delete')")
    public CommonResult<Boolean> deleteStreamProxy(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "app", required = false) String app,
            @RequestParam(value = "stream", required = false) String stream) {
        if (id != null) {
            videoStreamProxyService.deleteStreamProxy(id);
        } else if (app != null && stream != null) {
            videoStreamProxyService.deleteStreamProxy(app, stream);
        }
        return success(true);
    }

    public CommonResult<Boolean> deleteStreamProxy(Long id) {
        return deleteStreamProxy(id, null, null);
    }

    @GetMapping("/get")
    @Operation(summary = "获得拉流代理详情")
    @Parameter(name = "id", description = "代理 ID", required = true)
    @PreAuthorize("@ss.hasPermission('video:stream-proxy:query')")
    public CommonResult<VideoStreamProxyRespVO> getStreamProxy(@RequestParam("id") Long id) {
        VideoStreamProxyDO proxy = videoStreamProxyService.getStreamProxy(id);
        return success(VideoStreamProxyConvert.INSTANCE.convert(proxy));
    }

    @GetMapping("/page")
    @Operation(summary = "获得拉流代理分页")
    @PreAuthorize("@ss.hasPermission('video:stream-proxy:query')")
    public CommonResult<PageResult<VideoStreamProxyRespVO>> getStreamProxyPage(@Valid VideoStreamProxyPageReqVO pageReqVO) {
        PageResult<VideoStreamProxyDO> pageResult = videoStreamProxyService.getStreamProxyPage(pageReqVO);
        return success(VideoStreamProxyConvert.INSTANCE.convertPage(pageResult));
    }

    @PostMapping("/start")
    @Operation(summary = "启动拉流代理")
    @PreAuthorize("@ss.hasPermission('video:stream-proxy:update')")
    public CommonResult<Boolean> startStreamProxy(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "app", required = false) String app,
            @RequestParam(value = "stream", required = false) String stream) {
        if (id != null) {
            videoStreamProxyService.startStreamProxy(id);
        } else if (app != null && stream != null) {
            videoStreamProxyService.startStreamProxy(app, stream);
        }
        return success(true);
    }

    public CommonResult<Boolean> startStreamProxy(Long id) {
        return startStreamProxy(id, null, null);
    }

    @PostMapping("/stop")
    @Operation(summary = "停止拉流代理")
    @PreAuthorize("@ss.hasPermission('video:stream-proxy:update')")
    public CommonResult<Boolean> stopStreamProxy(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "app", required = false) String app,
            @RequestParam(value = "stream", required = false) String stream) {
        if (id != null) {
            videoStreamProxyService.stopStreamProxy(id);
        } else if (app != null && stream != null) {
            videoStreamProxyService.stopStreamProxy(app, stream);
        }
        return success(true);
    }

    public CommonResult<Boolean> stopStreamProxy(Long id) {
        return stopStreamProxy(id, null, null);
    }
}
