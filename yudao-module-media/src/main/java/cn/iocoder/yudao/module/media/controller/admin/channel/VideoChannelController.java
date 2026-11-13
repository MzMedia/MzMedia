package cn.iocoder.yudao.module.media.controller.admin.channel;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.media.controller.admin.channel.vo.VideoChannelPageReqVO;
import cn.iocoder.yudao.module.media.controller.admin.channel.vo.VideoChannelRespVO;
import cn.iocoder.yudao.module.media.convert.VideoChannelConvert;
import cn.iocoder.yudao.module.media.dal.dataobject.channel.VideoChannelDO;
import cn.iocoder.yudao.module.media.service.channel.VideoChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 视频通道管理 Controller
 * 路由前缀自动映射为 /admin-api/video/channel
 *
 * @author MzMedia Architect
 */
@Tag(name = "管理后台 - 视频通道管理")
@RestController
@RequestMapping("/video/channel")
@Validated
public class VideoChannelController {

    @Resource
    private VideoChannelService videoChannelService;

    @GetMapping("/get")
    @Operation(summary = "获得视频通道详情")
    @Parameter(name = "id", description = "通道主键ID", required = true, example = "1001")
    @PreAuthorize("@ss.hasPermission('media:channel:query')")
    public CommonResult<VideoChannelRespVO> getChannel(@RequestParam("id") Long id) {
        VideoChannelDO channel = videoChannelService.getChannel(id);
        return success(VideoChannelConvert.INSTANCE.convert(channel));
    }

    @GetMapping("/page")
    @Operation(summary = "获得视频通道分页列表")
    @PreAuthorize("@ss.hasPermission('media:channel:query')")
    public CommonResult<PageResult<VideoChannelRespVO>> getChannelPage(@Valid VideoChannelPageReqVO pageReqVO) {
        PageResult<VideoChannelDO> pageResult = videoChannelService.getChannelPage(pageReqVO);
        return success(VideoChannelConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/list-by-device")
    @Operation(summary = "获得指定设备下的所有通道列表")
    @Parameter(name = "deviceId", description = "设备国标编码", required = true, example = "34020000001110000001")
    @PreAuthorize("@ss.hasPermission('media:channel:query')")
    public CommonResult<List<VideoChannelRespVO>> getChannelListByDeviceId(@RequestParam("deviceId") String deviceId) {
        List<VideoChannelDO> list = videoChannelService.getChannelListByDeviceId(deviceId);
        return success(VideoChannelConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/map-list")
    @Operation(summary = "获得具有地理坐标的视频通道点位列表")
    @PreAuthorize("@ss.hasPermission('media:channel:query')")
    public CommonResult<List<VideoChannelRespVO>> getMapChannelList() {
        List<VideoChannelDO> list = videoChannelService.getChannelListWithCoordinates();
        return success(VideoChannelConvert.INSTANCE.convertList(list));
    }
}
