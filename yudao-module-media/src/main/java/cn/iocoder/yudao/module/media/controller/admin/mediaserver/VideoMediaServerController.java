package cn.iocoder.yudao.module.media.controller.admin.mediaserver;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.media.controller.admin.mediaserver.vo.VideoMediaServerPageReqVO;
import cn.iocoder.yudao.module.media.controller.admin.mediaserver.vo.VideoMediaServerRespVO;
import cn.iocoder.yudao.module.media.controller.admin.mediaserver.vo.VideoMediaServerSaveReqVO;
import cn.iocoder.yudao.module.media.convert.VideoMediaServerConvert;
import cn.iocoder.yudao.module.media.dal.dataobject.mediaserver.VideoMediaServerDO;
import cn.iocoder.yudao.module.media.service.mediaserver.VideoMediaServerService;
import cn.iocoder.yudao.module.media.media.bean.MediaServer;
import cn.iocoder.yudao.module.media.media.service.IMediaServerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 视频流媒体节点")
@RestController
@RequestMapping("/video/media-server")
@Validated
public class VideoMediaServerController {

    @Resource
    private VideoMediaServerService videoMediaServerService;

    @Resource
    private IMediaServerService legacyMediaServerService;

    @PostMapping("/save")
    @Operation(summary = "创建或更新流媒体节点")
    @PreAuthorize("@ss.hasPermission('video:media-server:update')")
    public CommonResult<Boolean> saveMediaServer(@Valid @RequestBody VideoMediaServerSaveReqVO saveReqVO) {
        videoMediaServerService.saveMediaServer(saveReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除流媒体节点")
    @Parameter(name = "id", description = "节点 ID", required = true)
    @PreAuthorize("@ss.hasPermission('video:media-server:delete')")
    public CommonResult<Boolean> deleteMediaServer(@RequestParam("id") String id) {
        videoMediaServerService.deleteMediaServer(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得流媒体节点详情")
    @Parameter(name = "id", description = "节点 ID", required = true)
    @PreAuthorize("@ss.hasPermission('video:media-server:query')")
    public CommonResult<VideoMediaServerRespVO> getMediaServer(@RequestParam("id") String id) {
        VideoMediaServerDO server = videoMediaServerService.getMediaServer(id);
        return success(VideoMediaServerConvert.INSTANCE.convert(server));
    }

    @GetMapping("/page")
    @Operation(summary = "获得流媒体节点分页")
    @PreAuthorize("@ss.hasPermission('video:media-server:query')")
    public CommonResult<PageResult<VideoMediaServerRespVO>> getMediaServerPage(@Valid VideoMediaServerPageReqVO pageReqVO) {
        PageResult<VideoMediaServerDO> pageResult = videoMediaServerService.getMediaServerPage(pageReqVO);
        return success(VideoMediaServerConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/list")
    @Operation(summary = "获得所有流媒体节点列表")
    @PreAuthorize("@ss.hasPermission('video:media-server:query')")
    public CommonResult<List<VideoMediaServerRespVO>> getMediaServerList() {
        List<VideoMediaServerDO> list = videoMediaServerService.getMediaServerList();
        return success(VideoMediaServerConvert.INSTANCE.convertList(list));
    }

    @GetMapping({"/online-list", "/list-online"})
    @Operation(summary = "获得所有在线流媒体节点列表")
    @PreAuthorize("@ss.hasPermission('video:media-server:query')")
    public CommonResult<List<VideoMediaServerRespVO>> getOnlineMediaServerList() {
        List<VideoMediaServerDO> list = videoMediaServerService.getOnlineList();
        return success(VideoMediaServerConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/check")
    @Operation(summary = "测试流媒体节点连通性")
    @PreAuthorize("@ss.hasPermission('video:media-server:query')")
    public CommonResult<MediaServer> checkMediaServer(
            @RequestParam("ip") String ip,
            @RequestParam("port") int port,
            @RequestParam("secret") String secret,
            @RequestParam(value = "type", required = false, defaultValue = "zlm") String type) {
        MediaServer server = legacyMediaServerService.checkMediaServer(ip, port, secret, type);
        return success(server);
    }

    @GetMapping("/system-info")
    @Operation(summary = "获得系统状态信息")
    @PreAuthorize("@ss.hasPermission('video:media-server:query')")
    public CommonResult<Map<String, Object>> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("cpu", 15);
        info.put("memory", 32);
        info.put("status", "running");
        return success(info);
    }

    @GetMapping("/load")
    @Operation(summary = "获得流媒体负载状态")
    @PreAuthorize("@ss.hasPermission('video:media-server:query')")
    public CommonResult<Map<String, Object>> getMediaServerLoad() {
        Map<String, Object> load = new HashMap<>();
        load.put("status", "normal");
        return success(load);
    }
}
