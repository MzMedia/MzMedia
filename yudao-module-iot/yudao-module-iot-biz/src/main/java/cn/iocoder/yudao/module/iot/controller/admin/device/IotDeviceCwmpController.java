package cn.iocoder.yudao.module.iot.controller.admin.device;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.iot.controller.admin.device.vo.cwmp.*;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceCwmpConfigDO;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceCwmpParamDO;
import cn.iocoder.yudao.module.iot.service.device.IotDeviceCwmpService;
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

@Tag(name = "管理后台 - IoT 设备 CWMP (TR-069) 管理")
@RestController
@RequestMapping("/iot/device-cwmp")
@Validated
public class IotDeviceCwmpController {

    @Resource
    private IotDeviceCwmpService cwmpService;

    @PostMapping("/config/save")
    @Operation(summary = "保存设备 CWMP 连接配置")
    @PreAuthorize("@ss.hasPermission('iot:device:update')")
    public CommonResult<Boolean> saveCwmpConfig(@Valid @RequestBody IotDeviceCwmpConfigSaveReqVO saveReqVO) {
        cwmpService.saveCwmpConfig(saveReqVO);
        return success(true);
    }

    @GetMapping("/config/get")
    @Operation(summary = "获得设备 CWMP 连接配置")
    @Parameter(name = "deviceId", description = "设备编号", required = true, example = "2048")
    @PreAuthorize("@ss.hasPermission('iot:device:query')")
    public CommonResult<IotDeviceCwmpConfigRespVO> getCwmpConfig(@RequestParam("deviceId") Long deviceId) {
        IotDeviceCwmpConfigDO config = cwmpService.getCwmpConfigByDeviceId(deviceId);
        return success(BeanUtils.toBean(config, IotDeviceCwmpConfigRespVO.class));
    }

    @GetMapping("/params")
    @Operation(summary = "获得设备 CWMP 参数列表")
    @Parameter(name = "deviceId", description = "设备编号", required = true, example = "2048")
    @Parameter(name = "pathPrefix", description = "路径前缀搜索", example = "InternetGatewayDevice.DeviceInfo.")
    @PreAuthorize("@ss.hasPermission('iot:device:query')")
    public CommonResult<List<IotDeviceCwmpParamRespVO>> getCwmpParams(
            @RequestParam("deviceId") Long deviceId,
            @RequestParam(value = "pathPrefix", required = false) String pathPrefix) {
        List<IotDeviceCwmpParamDO> list = cwmpService.getCwmpParams(deviceId, pathPrefix);
        return success(BeanUtils.toBean(list, IotDeviceCwmpParamRespVO.class));
    }

    @PostMapping("/params/refresh-names")
    @Operation(summary = "下发 GetParameterNames 发现参数树")
    @PreAuthorize("@ss.hasPermission('iot:device:update')")
    public CommonResult<Boolean> refreshParameterNames(
            @RequestParam("deviceId") Long deviceId,
            @RequestParam(value = "path", required = false) String path,
            @RequestParam(value = "nextLevel", defaultValue = "false") boolean nextLevel) {
        cwmpService.refreshParameterNames(deviceId, path, nextLevel);
        return success(true);
    }

    @PostMapping("/params/refresh-values")
    @Operation(summary = "下发 GetParameterValues 读取最新参数值")
    @PreAuthorize("@ss.hasPermission('iot:device:update')")
    public CommonResult<Boolean> refreshParameterValues(
            @RequestParam("deviceId") Long deviceId,
            @RequestBody List<String> paths) {
        cwmpService.refreshParameterValues(deviceId, paths);
        return success(true);
    }

    @PostMapping("/params/set")
    @Operation(summary = "下发 SetParameterValues 修改设备参数")
    @PreAuthorize("@ss.hasPermission('iot:device:update')")
    public CommonResult<Boolean> setParameterValues(@Valid @RequestBody IotDeviceCwmpSetParamReqVO reqVO) {
        cwmpService.setParameterValues(reqVO.getDeviceId(), reqVO.getParams());
        return success(true);
    }

    @PostMapping("/reboot")
    @Operation(summary = "远程重启设备")
    @Parameter(name = "deviceId", description = "设备编号", required = true, example = "2048")
    @PreAuthorize("@ss.hasPermission('iot:device:update')")
    public CommonResult<Boolean> reboot(@RequestParam("deviceId") Long deviceId) {
        cwmpService.reboot(deviceId);
        return success(true);
    }

    @PostMapping("/factory-reset")
    @Operation(summary = "恢复出厂设置")
    @Parameter(name = "deviceId", description = "设备编号", required = true, example = "2048")
    @PreAuthorize("@ss.hasPermission('iot:device:update')")
    public CommonResult<Boolean> factoryReset(@RequestParam("deviceId") Long deviceId) {
        cwmpService.factoryReset(deviceId);
        return success(true);
    }

    @PostMapping("/connection-request")
    @Operation(summary = "主动唤醒设备 (Connection Request)")
    @Parameter(name = "deviceId", description = "设备编号", required = true, example = "2048")
    @PreAuthorize("@ss.hasPermission('iot:device:update')")
    public CommonResult<Boolean> triggerConnectionRequest(@RequestParam("deviceId") Long deviceId) {
        boolean ok = cwmpService.triggerConnectionRequest(deviceId);
        return success(ok);
    }

    @GetMapping("/wifi")
    @Operation(summary = "获得设备 Wi-Fi 配置")
    @Parameter(name = "deviceId", description = "设备编号", required = true, example = "2048")
    @PreAuthorize("@ss.hasPermission('iot:device:query')")
    public CommonResult<IotDeviceCwmpWifiVO> getWifiConfig(@RequestParam("deviceId") Long deviceId) {
        return success(cwmpService.getWifiConfig(deviceId));
    }

    @PostMapping("/wifi")
    @Operation(summary = "修改设备 Wi-Fi 配置")
    @PreAuthorize("@ss.hasPermission('iot:device:update')")
    public CommonResult<Boolean> setWifiConfig(
            @RequestParam("deviceId") Long deviceId,
            @Valid @RequestBody IotDeviceCwmpWifiVO wifiVO) {
        cwmpService.setWifiConfig(deviceId, wifiVO);
        return success(true);
    }

    @PostMapping("/diagnostics/ping")
    @Operation(summary = "执行 Ping 诊断测试")
    @PreAuthorize("@ss.hasPermission('iot:device:update')")
    public CommonResult<Boolean> runPingDiagnostics(@Valid @RequestBody IotDeviceCwmpPingReqVO reqVO) {
        cwmpService.runPingDiagnostics(reqVO.getDeviceId(), reqVO.getHost(), reqVO.getNumberOfRepetitions());
        return success(true);
    }

}
