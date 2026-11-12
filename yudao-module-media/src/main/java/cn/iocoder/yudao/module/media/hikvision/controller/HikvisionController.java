package cn.iocoder.yudao.module.media.hikvision.controller;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.media.hikvision.bean.HikDevice;
import cn.iocoder.yudao.module.media.hikvision.service.IHikvisionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "海康威视 ISAPI 管理")
@Slf4j
@RestController
@RequestMapping({"/admin-api/media/hikvision", "/api/media/hikvision"})
public class HikvisionController {

    @Autowired
    private IHikvisionService hikService;

    @Operation(summary = "获取海康设备列表")
    @GetMapping("/list")
    public CommonResult<List<HikDevice>> list() {
        return success(hikService.listDevices());
    }

    @Operation(summary = "获取单个海康设备")
    @GetMapping("/get")
    public CommonResult<HikDevice> get(@RequestParam int id) {
        return success(hikService.getDevice(id));
    }

    @Operation(summary = "保存或更新海康设备")
    @PostMapping("/save")
    public CommonResult<HikDevice> save(@RequestBody HikDevice device) {
        return success(hikService.saveDevice(device));
    }

    @Operation(summary = "删除海康设备")
    @DeleteMapping("/delete")
    public CommonResult<Boolean> delete(@RequestParam int id) {
        return success(hikService.deleteDevice(id));
    }

    @Operation(summary = "测试云台控制")
    @PostMapping("/ptz")
    public CommonResult<Boolean> ptz(@RequestParam int id,
                                     @RequestParam(defaultValue = "1") int channelNo,
                                     @RequestParam(required = false) Integer pan,
                                     @RequestParam(required = false) Integer tilt,
                                     @RequestParam(required = false) Integer zoom,
                                     @RequestParam(required = false, defaultValue = "50") Integer speed) {
        return success(hikService.ptzControl(id, channelNo, pan, tilt, zoom, speed));
    }

    @Operation(summary = "现场抓拍")
    @GetMapping(value = "/capture", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> capture(@RequestParam int id, @RequestParam(defaultValue = "1") int channelNo) {
        byte[] bytes = hikService.capturePicture(id, channelNo);
        if (bytes == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
    }
}
