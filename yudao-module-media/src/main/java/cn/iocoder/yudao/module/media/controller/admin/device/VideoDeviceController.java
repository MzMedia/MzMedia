package cn.iocoder.yudao.module.media.controller.admin.device;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.media.controller.admin.device.vo.VideoDevicePageReqVO;
import cn.iocoder.yudao.module.media.controller.admin.device.vo.VideoDeviceRespVO;
import cn.iocoder.yudao.module.media.controller.admin.device.vo.VideoDeviceSaveReqVO;
import cn.iocoder.yudao.module.media.convert.VideoDeviceConvert;
import cn.iocoder.yudao.module.media.dal.dataobject.device.VideoDeviceDO;
import cn.iocoder.yudao.module.media.service.device.VideoDeviceService;
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
 * 视频接入设备管理 Controller
 * 对齐 ruoyi-vue-pro 标准：路由前缀自动映射为 /admin-api/video/device
 *
 * @author MzMedia Architect
 */
@Tag(name = "管理后台 - 视频接入设备")
@RestController
@RequestMapping("/video/device")
@Validated
public class VideoDeviceController {

    @Resource
    private VideoDeviceService videoDeviceService;

    @PostMapping("/create")
    @Operation(summary = "创建视频接入设备")
    @PreAuthorize("@ss.hasPermission('media:device:create')")
    public CommonResult<Long> createDevice(@Valid @RequestBody VideoDeviceSaveReqVO createReqVO) {
        Long deviceId = videoDeviceService.createDevice(createReqVO);
        return success(deviceId);
    }

    @PutMapping("/update")
    @Operation(summary = "修改视频接入设备")
    @PreAuthorize("@ss.hasPermission('media:device:update')")
    public CommonResult<Boolean> updateDevice(@Valid @RequestBody VideoDeviceSaveReqVO updateReqVO) {
        videoDeviceService.updateDevice(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除视频接入设备")
    @Parameter(name = "id", description = "设备主键ID", example = "101")
    @Parameter(name = "deviceId", description = "设备国标编号", example = "34020000001110000001")
    @PreAuthorize("@ss.hasPermission('media:device:delete')")
    public CommonResult<Boolean> deleteDevice(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "deviceId", required = false) String deviceId) {
        if (id != null) {
            videoDeviceService.deleteDevice(id);
        } else if (deviceId != null) {
            videoDeviceService.deleteDeviceByCode(deviceId);
        }
        return success(true);
    }

    public CommonResult<Boolean> deleteDevice(Long id) {
        return deleteDevice(id, null);
    }

    @GetMapping("/get")
    @Operation(summary = "获得视频设备详情")
    @Parameter(name = "id", description = "设备主键ID", example = "101")
    @Parameter(name = "deviceId", description = "设备国标编号", example = "34020000001110000001")
    @PreAuthorize("@ss.hasPermission('media:device:query')")
    public CommonResult<VideoDeviceRespVO> getDevice(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "deviceId", required = false) String deviceId) {
        VideoDeviceDO device = id != null ? videoDeviceService.getDevice(id) : videoDeviceService.getDeviceByDeviceId(deviceId);
        return success(VideoDeviceConvert.INSTANCE.convert(device));
    }

    public CommonResult<VideoDeviceRespVO> getDevice(Long id) {
        return getDevice(id, null);
    }

    @GetMapping("/list")
    @Operation(summary = "获得所有视频设备列表")
    @PreAuthorize("@ss.hasPermission('media:device:query')")
    public CommonResult<List<VideoDeviceRespVO>> getDeviceList() {
        List<VideoDeviceDO> list = videoDeviceService.getDeviceList();
        return success(VideoDeviceConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获得视频设备分页列表")
    @PreAuthorize("@ss.hasPermission('media:device:query')")
    public CommonResult<PageResult<VideoDeviceRespVO>> getDevicePage(@Valid VideoDevicePageReqVO pageReqVO) {
        PageResult<VideoDeviceDO> pageResult = videoDeviceService.getDevicePage(pageReqVO);
        return success(VideoDeviceConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/list-online")
    @Operation(summary = "获得所有在线视频设备列表")
    @PreAuthorize("@ss.hasPermission('media:device:query')")
    public CommonResult<List<VideoDeviceRespVO>> getOnlineDeviceList() {
        List<VideoDeviceDO> list = videoDeviceService.getOnlineDeviceList();
        return success(VideoDeviceConvert.INSTANCE.convertList(list));
    }

    @PostMapping("/sync-channels")
    @Operation(summary = "主动同步设备通道目录 (发送 SIP Catalog 信令)")
    @Parameter(name = "deviceId", description = "设备国标编码", required = true, example = "34020000001110000001")
    @PreAuthorize("@ss.hasPermission('media:device:update')")
    public CommonResult<Boolean> syncDeviceChannels(@RequestParam("deviceId") String deviceId) {
        videoDeviceService.syncDeviceChannels(deviceId);
        return success(true);
    }
}
