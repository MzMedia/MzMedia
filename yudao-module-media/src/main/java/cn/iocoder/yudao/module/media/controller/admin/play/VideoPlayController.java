package cn.iocoder.yudao.module.media.controller.admin.play;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.media.controller.admin.play.vo.VideoPlayReqVO;
import cn.iocoder.yudao.module.media.controller.admin.play.vo.VideoPlayRespVO;
import cn.iocoder.yudao.module.media.service.play.VideoPlayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 视频实时点播拉流 Controller
 * 路由前缀自动映射为 /admin-api/video/play
 *
 * @author MzMedia Architect
 */
@Tag(name = "管理后台 - 视频实时点播拉流")
@RestController
@RequestMapping("/video/play")
@Validated
public class VideoPlayController {

    @Resource
    private VideoPlayService videoPlayService;

    @RequestMapping(value = "/start", method = {RequestMethod.POST, RequestMethod.GET})
    @Operation(summary = "发起实时点播拉流", description = "支持 GB28181 国标信令拉流与 RTSP/RTMP 代理流拉流，返回全协议播放 URL 集合")
    @PreAuthorize("@ss.hasPermission('media:play:start')")
    public CommonResult<VideoPlayRespVO> startPlay(
            @RequestBody(required = false) VideoPlayReqVO reqVO,
            @RequestParam(value = "channelId", required = false) Long channelId) {
        if (reqVO == null) {
            reqVO = new VideoPlayReqVO();
        }
        if (channelId != null) {
            reqVO.setChannelId(channelId);
        }
        VideoPlayRespVO playUrls = videoPlayService.startPlay(reqVO);
        return success(playUrls);
    }

    public CommonResult<VideoPlayRespVO> startPlay(VideoPlayReqVO reqVO) {
        return startPlay(reqVO, null);
    }

    @RequestMapping(value = "/stop", method = {RequestMethod.POST, RequestMethod.GET})
    @Operation(summary = "停止实时点播/断开推拉流")
    @Parameter(name = "channelId", description = "通道自增主键ID", example = "1001")
    @PreAuthorize("@ss.hasPermission('media:play:stop')")
    public CommonResult<Boolean> stopPlay(
            @RequestBody(required = false) VideoPlayReqVO reqVO,
            @RequestParam(value = "channelId", required = false) Long channelId) {
        Long targetChannelId = channelId;
        if (targetChannelId == null && reqVO != null) {
            targetChannelId = reqVO.getChannelId();
        }
        if (targetChannelId != null) {
            videoPlayService.stopPlay(targetChannelId);
        }
        return success(true);
    }

    public CommonResult<Boolean> stopPlay(Long channelId) {
        return stopPlay(null, channelId);
    }
}
