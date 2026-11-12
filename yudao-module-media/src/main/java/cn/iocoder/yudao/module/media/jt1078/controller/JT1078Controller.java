package cn.iocoder.yudao.module.media.jt1078.controller;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.media.jt1078.bean.JT1078Device;
import cn.iocoder.yudao.module.media.jt1078.service.IJT1078Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "JT1078 车载音视频管理")
@Slf4j
@RestController
@RequestMapping({"/admin-api/media/jt1078", "/api/media/jt1078"})
public class JT1078Controller {

    @Autowired
    private IJT1078Service jt1078Service;

    @Operation(summary = "获取JT1078车载设备列表")
    @GetMapping("/list")
    public CommonResult<List<JT1078Device>> list() {
        return success(jt1078Service.listDevices());
    }

    @Operation(summary = "获取单个JT1078车载设备")
    @GetMapping("/get")
    public CommonResult<JT1078Device> get(@RequestParam int id) {
        return success(jt1078Service.getDevice(id));
    }

    @Operation(summary = "保存或更新JT1078车载设备")
    @PostMapping("/save")
    public CommonResult<JT1078Device> save(@RequestBody JT1078Device device) {
        return success(jt1078Service.saveDevice(device));
    }

    @Operation(summary = "删除JT1078车载设备")
    @DeleteMapping("/delete")
    public CommonResult<Boolean> delete(@RequestParam int id) {
        return success(jt1078Service.deleteDevice(id));
    }

    @Operation(summary = "车载云台控制")
    @PostMapping("/ptz")
    public CommonResult<Boolean> ptz(@RequestParam String simNo,
                                     @RequestParam(defaultValue = "1") int channelNo,
                                     @RequestParam(defaultValue = "0") int ptzCmd,
                                     @RequestParam(defaultValue = "50") int speed) {
        return success(jt1078Service.ptzControl(simNo, channelNo, ptzCmd, speed));
    }
}
