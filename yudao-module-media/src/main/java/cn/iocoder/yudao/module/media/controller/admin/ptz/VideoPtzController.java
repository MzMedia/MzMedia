package cn.iocoder.yudao.module.media.controller.admin.ptz;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.media.enums.VideoErrorCodeConstants;
import cn.iocoder.yudao.module.media.gb28181.bean.Device;
import cn.iocoder.yudao.module.media.gb28181.service.IDeviceService;
import cn.iocoder.yudao.module.media.gb28181.service.IPTZService;
import cn.iocoder.yudao.module.media.gb28181.transmit.cmd.impl.SIPCommander;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 视频云台与前端控制 Controller
 *
 * @author MzMedia Architect
 */
@Tag(name = "管理后台 - 视频云台控制")
@Slf4j
@RestController
@RequestMapping("/video/ptz")
@Validated
public class VideoPtzController {

    @Resource
    private SIPCommander sipCommander;

    @Resource
    private IDeviceService legacyDeviceService;

    @Resource
    private IPTZService ptzService;

    @RequestMapping(value = "/control", method = {RequestMethod.POST, RequestMethod.GET})
    @Operation(summary = "云台方向与变倍控制")
    @PreAuthorize("@ss.hasPermission('media:channel:update')")
    public CommonResult<Boolean> ptzControl(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("channelId") String channelId,
            @RequestParam("cmdCode") int cmdCode,
            @RequestParam(value = "horizonSpeed", defaultValue = "50") int horizonSpeed,
            @RequestParam(value = "verticalSpeed", defaultValue = "50") int verticalSpeed,
            @RequestParam(value = "zoomSpeed", defaultValue = "50") int zoomSpeed) {
        Device device = legacyDeviceService.getDeviceByDeviceId(deviceId);
        if (device == null) {
            throw ServiceExceptionUtil.exception(VideoErrorCodeConstants.VIDEO_DEVICE_NOT_EXISTS);
        }
        ptzService.ptz(device, channelId, cmdCode, horizonSpeed, verticalSpeed, zoomSpeed);
        return success(true);
    }

    @RequestMapping(value = "/iris", method = {RequestMethod.POST, RequestMethod.GET})
    @Operation(summary = "光圈控制")
    @PreAuthorize("@ss.hasPermission('media:channel:update')")
    public CommonResult<Boolean> irisControl(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("channelId") String channelId,
            @RequestParam("cmdCode") int cmdCode,
            @RequestParam(value = "speed", defaultValue = "50") int speed) {
        Device device = legacyDeviceService.getDeviceByDeviceId(deviceId);
        if (device == null) {
            throw ServiceExceptionUtil.exception(VideoErrorCodeConstants.VIDEO_DEVICE_NOT_EXISTS);
        }
        ptzService.frontEndCommand(device, channelId, cmdCode, 0, speed, 0);
        return success(true);
    }

    @RequestMapping(value = "/focus", method = {RequestMethod.POST, RequestMethod.GET})
    @Operation(summary = "聚焦控制")
    @PreAuthorize("@ss.hasPermission('media:channel:update')")
    public CommonResult<Boolean> focusControl(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("channelId") String channelId,
            @RequestParam("cmdCode") int cmdCode,
            @RequestParam(value = "speed", defaultValue = "50") int speed) {
        Device device = legacyDeviceService.getDeviceByDeviceId(deviceId);
        if (device == null) {
            throw ServiceExceptionUtil.exception(VideoErrorCodeConstants.VIDEO_DEVICE_NOT_EXISTS);
        }
        ptzService.frontEndCommand(device, channelId, cmdCode, speed, 0, 0);
        return success(true);
    }

    @GetMapping("/preset/query")
    @Operation(summary = "查询预置位列表")
    @PreAuthorize("@ss.hasPermission('media:channel:query')")
    public CommonResult<Boolean> queryPresets(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("channelId") String channelId) {
        Device device = legacyDeviceService.getDeviceByDeviceId(deviceId);
        if (device == null) {
            throw ServiceExceptionUtil.exception(VideoErrorCodeConstants.VIDEO_DEVICE_NOT_EXISTS);
        }
        try {
            sipCommander.presetQuery(device, channelId, null);
        } catch (Exception e) {
            log.error("[VideoPtzController] 查询预置位异常", e);
        }
        return success(true);
    }

    @RequestMapping(value = "/preset/call", method = {RequestMethod.POST, RequestMethod.GET})
    @Operation(summary = "调用预置位")
    @PreAuthorize("@ss.hasPermission('media:channel:update')")
    public CommonResult<Boolean> callPreset(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("channelId") String channelId,
            @RequestParam("presetId") int presetId) {
        Device device = legacyDeviceService.getDeviceByDeviceId(deviceId);
        if (device == null) {
            throw ServiceExceptionUtil.exception(VideoErrorCodeConstants.VIDEO_DEVICE_NOT_EXISTS);
        }
        // 129 对应国标调用预置位
        ptzService.frontEndCommand(device, channelId, 129, 0, presetId, 0);
        return success(true);
    }

    @RequestMapping(value = "/preset/add", method = {RequestMethod.POST, RequestMethod.GET})
    @Operation(summary = "添加预置位")
    @PreAuthorize("@ss.hasPermission('media:channel:update')")
    public CommonResult<Boolean> addPreset(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("channelId") String channelId,
            @RequestParam(value = "presetName", required = false) String presetName,
            @RequestParam("presetId") int presetId) {
        Device device = legacyDeviceService.getDeviceByDeviceId(deviceId);
        if (device == null) {
            throw ServiceExceptionUtil.exception(VideoErrorCodeConstants.VIDEO_DEVICE_NOT_EXISTS);
        }
        // 130 对应国标设置预置位
        ptzService.frontEndCommand(device, channelId, 130, 0, presetId, 0);
        return success(true);
    }
}
