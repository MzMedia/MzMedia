package cn.iocoder.yudao.module.media.onvif.controller;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.media.onvif.bean.OnvifDevice;
import cn.iocoder.yudao.module.media.onvif.service.IOnvifService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "ONVIF 设备管理")
@Slf4j
@RestController
@RequestMapping("/api/media/onvif")
public class OnvifController {

    @Autowired
    private IOnvifService onvifService;

    @Operation(summary = "局域网探测搜索 ONVIF 设备")
    @GetMapping("/discover")
    public CommonResult<List<OnvifDevice>> discover(@RequestParam(defaultValue = "3000") int timeoutMs) {
        return success(onvifService.discover(timeoutMs));
    }

    @Operation(summary = "获取所有 ONVIF 设备列表")
    @GetMapping("/list")
    public CommonResult<List<OnvifDevice>> list() {
        return success(onvifService.listDevices());
    }

    @Operation(summary = "获取单个 ONVIF 设备详情")
    @GetMapping("/get")
    public CommonResult<OnvifDevice> get(@RequestParam int id) {
        return success(onvifService.getDevice(id));
    }

    @Operation(summary = "保存或更新 ONVIF 设备")
    @PostMapping("/save")
    public CommonResult<OnvifDevice> save(@RequestBody OnvifDevice device) {
        return success(onvifService.saveDevice(device));
    }

    @Operation(summary = "删除 ONVIF 设备")
    @DeleteMapping("/delete")
    public CommonResult<Boolean> delete(@RequestParam int id) {
        return success(onvifService.deleteDevice(id));
    }

    @Operation(summary = "测试云台控制")
    @PostMapping("/ptz")
    public CommonResult<Boolean> ptz(@RequestParam int id,
                                     @RequestParam(required = false) Integer pan,
                                     @RequestParam(required = false) Integer tilt,
                                     @RequestParam(required = false) Integer zoom,
                                     @RequestParam(required = false, defaultValue = "50") Double speed) {
        return success(onvifService.ptzControl(id, pan, tilt, zoom, speed));
    }
}
