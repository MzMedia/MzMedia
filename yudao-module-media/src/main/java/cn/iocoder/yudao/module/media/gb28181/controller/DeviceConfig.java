package cn.iocoder.yudao.module.media.gb28181.controller;

import cn.iocoder.yudao.module.media.conf.security.JwtUtils;
import cn.iocoder.yudao.module.media.gb28181.bean.*;
import cn.iocoder.yudao.module.media.gb28181.service.IDeviceService;
import cn.iocoder.yudao.module.media.vmanager.bean.ErrorCode;
import cn.iocoder.yudao.module.media.vmanager.bean.MzResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

@Slf4j
@Tag(name = "国标设备配置")
@RestController
@RequestMapping({"/admin-api/api/device/config", "/api/device/config"})
public class DeviceConfig {

    @Autowired
    private IDeviceService deviceService;

    @GetMapping("/set/basicParam")
    @Operation(summary = "设置基本配置", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @Parameter(name = "basicParam", description = "基础配置参数", required = true)
    public DeferredResult<MzResult<String>> homePositionApi(BasicParam basicParam) {
        if (log.isDebugEnabled()) {
            log.debug("基本配置设置命令API调用");
        }
        Assert.notNull(basicParam.getDeviceId(), "设备ID必须存在");

        Device device = deviceService.getDeviceByDeviceId(basicParam.getDeviceId());
        Assert.notNull(device, "设备不存在");

        DeferredResult<MzResult<String>> deferredResult = new DeferredResult<>();
        deviceService.deviceBasicConfig(device, basicParam, (code, msg, data) -> {
            deferredResult.setResult(new MzResult<>(code, msg, data));
        });

        deferredResult.onTimeout(() -> {
            log.warn("[设备配置] 超时, {}", device.getDeviceId());
            deferredResult.setResult(MzResult.fail(ErrorCode.ERROR100.getCode(), "超时"));
        });
        return deferredResult;
    }

    @GetMapping("/set/videoParamOpt")
    @Operation(summary = "设置视频参数", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @Parameter(name = "videoParamOpt", description = "视频参数", required = true)
    public DeferredResult<MzResult<String>> setVideoParamOpt(VideoParamOpt videoParamOpt) {
        if (log.isDebugEnabled()) {
            log.debug("视频参数设置命令API调用");
        }
        Assert.notNull(videoParamOpt.getDeviceId(), "设备ID必须存在");

        Device device = deviceService.getDeviceByDeviceId(videoParamOpt.getDeviceId());
        Assert.notNull(device, "设备不存在");

        DeferredResult<MzResult<String>> deferredResult = new DeferredResult<>();
        deviceService.deviceVideoParamConfig(device, videoParamOpt, (code, msg, data) -> {
            deferredResult.setResult(new MzResult<>(code, msg, data));
        });

        deferredResult.onTimeout(() -> {
            log.warn("[设备配置] 超时, {}", device.getDeviceId());
            deferredResult.setResult(MzResult.fail(ErrorCode.ERROR100.getCode(), "超时"));
        });
        return deferredResult;
    }

    @Operation(summary = "查询基本参数配置", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @Parameter(name = "deviceId", description = "设备国标编号", required = true)
    @Parameter(name = "channelId", description = "通道国标编号")
    @GetMapping("/query/basicParam")
    public DeferredResult<MzResult<BasicParam>> queryBasicParam(String deviceId,
                                                                  @RequestParam(required = false) String channelId) {
        Device device = deviceService.getDeviceByDeviceId(deviceId);
        Assert.notNull(device, "设备不存在");
        DeferredResult<MzResult<BasicParam>> deferredResult = new DeferredResult<>();
        deviceService.deviceConfigQuery(device, channelId, BasicParam.class, (code, msg, data) -> {
            data.setDeviceId(deviceId);
            data.setChannelId(channelId);
            deferredResult.setResult(new MzResult<>(code, msg, data));
        });
        deferredResult.onTimeout(() -> {
            log.warn("[获取设备配置] 超时, {}", device.getDeviceId());
            deferredResult.setResult(MzResult.fail(ErrorCode.ERROR100.getCode(), "超时"));
        });
        return deferredResult;
    }

    @Operation(summary = "查询视频参数范围", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @Parameter(name = "deviceId", description = "设备国标编号", required = true)
    @Parameter(name = "channelId", description = "通道国标编号")
    @GetMapping("/query/videoParamOpt")
    public DeferredResult<MzResult<VideoParamOpt>> queryVideoParamOpt(String deviceId,
                                                                        @RequestParam(required = false) String channelId) {
        Device device = deviceService.getDeviceByDeviceId(deviceId);
        Assert.notNull(device, "设备不存在");
        DeferredResult<MzResult<VideoParamOpt>> deferredResult = new DeferredResult<>();
        deviceService.deviceConfigQuery(device, channelId, VideoParamOpt.class, (code, msg, data) -> {
            deferredResult.setResult(new MzResult<>(code, msg, data));
        });
        deferredResult.onTimeout(() -> {
            log.warn("[获取设备配置] 超时, {}", device.getDeviceId());
            deferredResult.setResult(MzResult.fail(ErrorCode.ERROR100.getCode(), "超时"));
        });
        return deferredResult;
    }

    @Operation(summary = "查询SVAC编码配置", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @Parameter(name = "deviceId", description = "设备国标编号", required = true)
    @Parameter(name = "channelId", description = "通道国标编号")
    @GetMapping("/query/svacEncodeConfig")
    public DeferredResult<MzResult<SVACEncodeConfig>> querySVACEncodeConfig(String deviceId,
                                                                              @RequestParam(required = false) String channelId) {
        Device device = deviceService.getDeviceByDeviceId(deviceId);
        Assert.notNull(device, "设备不存在");
        DeferredResult<MzResult<SVACEncodeConfig>> deferredResult = new DeferredResult<>();
        deviceService.deviceConfigQuery(device, channelId, SVACEncodeConfig.class, (code, msg, data) -> {
            deferredResult.setResult(new MzResult<>(code, msg, data));
        });
        deferredResult.onTimeout(() -> {
            log.warn("[获取设备配置] 超时, {}", device.getDeviceId());
            deferredResult.setResult(MzResult.fail(ErrorCode.ERROR100.getCode(), "超时"));
        });
        return deferredResult;
    }

    @Operation(summary = "查询SVAC解码配置", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @Parameter(name = "deviceId", description = "设备国标编号", required = true)
    @Parameter(name = "channelId", description = "通道国标编号")
    @GetMapping("/query/svacDecodeConfig")
    public DeferredResult<MzResult<SVACDecodeConfig>> querySVACDecodeConfig(String deviceId,
                                                                              @RequestParam(required = false) String channelId) {
        Device device = deviceService.getDeviceByDeviceId(deviceId);
        Assert.notNull(device, "设备不存在");
        DeferredResult<MzResult<SVACDecodeConfig>> deferredResult = new DeferredResult<>();
        deviceService.deviceConfigQuery(device, channelId, SVACDecodeConfig.class, (code, msg, data) -> {
            deferredResult.setResult(new MzResult<>(code, msg, data));
        });
        deferredResult.onTimeout(() -> {
            log.warn("[获取设备配置] 超时, {}", device.getDeviceId());
            deferredResult.setResult(MzResult.fail(ErrorCode.ERROR100.getCode(), "超时"));
        });
        return deferredResult;
    }

}
