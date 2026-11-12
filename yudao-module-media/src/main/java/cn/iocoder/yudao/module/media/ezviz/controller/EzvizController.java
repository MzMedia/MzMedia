package cn.iocoder.yudao.module.media.ezviz.controller;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.media.ezviz.bean.EzvizDevice;
import cn.iocoder.yudao.module.media.ezviz.service.IEzvizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "萤石开放云平台设备管理")
@Slf4j
@RestController
@RequestMapping({"/admin-api/media/ezviz", "/api/media/ezviz"})
public class EzvizController {

    @Autowired
    private IEzvizService ezvizService;

    @Operation(summary = "获取萤石云设备列表")
    @GetMapping("/list")
    public CommonResult<List<EzvizDevice>> list() {
        return success(ezvizService.listDevices());
    }

    @Operation(summary = "获取单个萤石云设备")
    @GetMapping("/get")
    public CommonResult<EzvizDevice> get(@RequestParam int id) {
        return success(ezvizService.getDevice(id));
    }

    @Operation(summary = "保存或更新萤石云设备")
    @PostMapping("/save")
    public CommonResult<EzvizDevice> save(@RequestBody EzvizDevice device) {
        return success(ezvizService.saveDevice(device));
    }

    @Operation(summary = "删除萤石云设备")
    @DeleteMapping("/delete")
    public CommonResult<Boolean> delete(@RequestParam int id) {
        return success(ezvizService.deleteDevice(id));
    }

    @Operation(summary = "获取萤石云直播地址")
    @GetMapping("/live-url")
    public CommonResult<String> getLiveUrl(@RequestParam int id) {
        return success(ezvizService.getLiveUrl(id));
    }

    @Operation(summary = "萤石云台控制")
    @PostMapping("/ptz")
    public CommonResult<Boolean> ptz(@RequestParam int id,
                                     @RequestParam(defaultValue = "0") int direction,
                                     @RequestParam(defaultValue = "1") int speed,
                                     @RequestParam(defaultValue = "false") boolean stop) {
        return success(ezvizService.ptzControl(id, direction, speed, stop));
    }

    @Operation(summary = "萤石云抓拍")
    @PostMapping("/capture")
    public CommonResult<String> capture(@RequestParam int id) {
        return success(ezvizService.capturePicture(id));
    }
}
