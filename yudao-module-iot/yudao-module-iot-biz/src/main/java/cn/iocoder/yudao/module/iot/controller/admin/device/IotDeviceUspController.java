package cn.iocoder.yudao.module.iot.controller.admin.device;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.iot.controller.admin.device.vo.usp.*;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceUspConfigDO;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceUspParamDO;
import cn.iocoder.yudao.module.iot.service.device.IotDeviceUspService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - IoT 设备 USP (TR-369) 管理")
@RestController
@RequestMapping("/iot/device-usp")
@Validated
public class IotDeviceUspController {

    @Resource
    private IotDeviceUspService uspService;

    @PostMapping("/config/save")
    @Operation(summary = "保存设备 USP 连接配置")
    @PreAuthorize("@ss.hasPermission('iot:device:update')")
    public CommonResult<Boolean> saveUspConfig(@Valid @RequestBody IotDeviceUspConfigSaveReqVO saveReqVO) {
        uspService.saveUspConfig(saveReqVO);
        return success(true);
    }

    @GetMapping("/config/get")
    @Operation(summary = "获得设备 USP 连接配置")
    @Parameter(name = "deviceId", description = "设备编号", required = true, example = "2048")
    @PreAuthorize("@ss.hasPermission('iot:device:query')")
    public CommonResult<IotDeviceUspConfigRespVO> getUspConfig(@RequestParam("deviceId") Long deviceId) {
        IotDeviceUspConfigDO config = uspService.getUspConfigByDeviceId(deviceId);
        return success(BeanUtils.toBean(config, IotDeviceUspConfigRespVO.class));
    }

    @GetMapping("/params")
    @Operation(summary = "获得设备 USP 参数列表")
    @Parameter(name = "deviceId", description = "设备编号", required = true, example = "2048")
    @Parameter(name = "pathPrefix", description = "路径前缀搜索", example = "Device.WiFi.")
    @PreAuthorize("@ss.hasPermission('iot:device:query')")
    public CommonResult<List<IotDeviceUspParamRespVO>> getUspParams(
            @RequestParam("deviceId") Long deviceId,
            @RequestParam(value = "pathPrefix", required = false) String pathPrefix) {
        List<IotDeviceUspParamDO> list = uspService.getUspParams(deviceId, pathPrefix);
        return success(BeanUtils.toBean(list, IotDeviceUspParamRespVO.class));
    }

    @PostMapping("/get")
    @Operation(summary = "下发 USP GET 请求获取最新参数值")
    @PreAuthorize("@ss.hasPermission('iot:device:update')")
    public CommonResult<Boolean> uspGet(
            @RequestParam("deviceId") Long deviceId,
            @RequestBody List<String> paths) {
        uspService.uspGet(deviceId, paths);
        return success(true);
    }

    @PostMapping("/set")
    @Operation(summary = "下发 USP SET 请求设置参数")
    @PreAuthorize("@ss.hasPermission('iot:device:update')")
    public CommonResult<Boolean> uspSet(
            @RequestParam("deviceId") Long deviceId,
            @RequestBody Map<String, String> params) {
        uspService.uspSet(deviceId, params);
        return success(true);
    }

    @PostMapping("/operate")
    @Operation(summary = "下发 USP OPERATE 执行操作命令")
    @PreAuthorize("@ss.hasPermission('iot:device:update')")
    public CommonResult<Boolean> uspOperate(@Valid @RequestBody IotDeviceUspOperateReqVO reqVO) {
        uspService.uspOperate(reqVO.getDeviceId(), reqVO.getCommand(), reqVO.getInputArgs());
        return success(true);
    }

    @PostMapping("/reboot")
    @Operation(summary = "远程重启设备 (Device.Reboot)")
    @Parameter(name = "deviceId", description = "设备编号", required = true, example = "2048")
    @PreAuthorize("@ss.hasPermission('iot:device:update')")
    public CommonResult<Boolean> reboot(@RequestParam("deviceId") Long deviceId) {
        uspService.reboot(deviceId);
        return success(true);
    }

    @PostMapping("/factory-reset")
    @Operation(summary = "恢复出厂设置 (Device.FactoryReset)")
    @Parameter(name = "deviceId", description = "设备编号", required = true, example = "2048")
    @PreAuthorize("@ss.hasPermission('iot:device:update')")
    public CommonResult<Boolean> factoryReset(@RequestParam("deviceId") Long deviceId) {
        uspService.factoryReset(deviceId);
        return success(true);
    }

}
