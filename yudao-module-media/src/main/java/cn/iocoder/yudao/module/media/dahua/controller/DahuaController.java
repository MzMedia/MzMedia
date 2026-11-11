package cn.iocoder.yudao.module.media.dahua.controller;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.media.dahua.bean.DahuaDevice;
import cn.iocoder.yudao.module.media.dahua.service.IDahuaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "大华设备管理")
@Slf4j
@RestController
@RequestMapping("/api/media/dahua")
public class DahuaController {

    @Autowired
    private IDahuaService dahuaService;

    @Operation(summary = "获取大华设备列表")
    @GetMapping("/list")
    public CommonResult<List<DahuaDevice>> list() {
        return success(dahuaService.listDevices());
    }

    @Operation(summary = "获取单个大华设备")
    @GetMapping("/get")
    public CommonResult<DahuaDevice> get(@RequestParam int id) {
        return success(dahuaService.getDevice(id));
    }

    @Operation(summary = "保存或更新大华设备")
    @PostMapping("/save")
    public CommonResult<DahuaDevice> save(@RequestBody DahuaDevice device) {
        return success(dahuaService.saveDevice(device));
    }

    @Operation(summary = "删除大华设备")
    @DeleteMapping("/delete")
    public CommonResult<Boolean> delete(@RequestParam int id) {
        return success(dahuaService.deleteDevice(id));
    }

    @Operation(summary = "云台控制")
    @PostMapping("/ptz")
    public CommonResult<Boolean> ptz(@RequestParam int id,
                                     @RequestParam(defaultValue = "1") int channelNo,
                                     @RequestParam(defaultValue = "start") String action,
                                     @RequestParam(defaultValue = "Up") String code,
                                     @RequestParam(defaultValue = "5") int speed) {
        return success(dahuaService.ptzControl(id, channelNo, action, code, speed));
    }

    @Operation(summary = "现场抓拍")
    @GetMapping(value = "/capture", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> capture(@RequestParam int id, @RequestParam(defaultValue = "1") int channelNo) {
        byte[] bytes = dahuaService.capturePicture(id, channelNo);
        if (bytes == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
    }
}
