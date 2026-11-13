package cn.iocoder.yudao.module.media.service.device;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.media.controller.admin.device.vo.VideoDevicePageReqVO;
import cn.iocoder.yudao.module.media.controller.admin.device.vo.VideoDeviceSaveReqVO;
import cn.iocoder.yudao.module.media.convert.VideoDeviceConvert;
import cn.iocoder.yudao.module.media.dal.dataobject.device.VideoDeviceDO;
import cn.iocoder.yudao.module.media.dal.mysql.device.VideoDeviceMapper;
import cn.iocoder.yudao.module.media.enums.VideoErrorCodeConstants;
import cn.iocoder.yudao.module.media.gb28181.bean.Device;
import cn.iocoder.yudao.module.media.gb28181.service.IDeviceService;
import cn.iocoder.yudao.module.media.gb28181.transmit.cmd.ISIPCommander;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 视频设备服务实现类
 *
 * @author MzMedia Architect
 */
@Slf4j
@Service
public class VideoDeviceServiceImpl implements VideoDeviceService {

    @Resource
    private VideoDeviceMapper videoDeviceMapper;

    @Resource
    @Lazy
    private IDeviceService legacyDeviceService;

    @Resource
    @Lazy
    private ISIPCommander sipCommander;

    @Override
    public Long createDevice(VideoDeviceSaveReqVO createReqVO) {
        // 校验设备国标编码是否已存在
        validateDeviceCodeUnique(null, createReqVO.getDeviceId());

        VideoDeviceDO device = VideoDeviceConvert.INSTANCE.convert(createReqVO);
        device.setOnLine(0);
        videoDeviceMapper.insert(device);
        return device.getId();
    }

    @Override
    public void updateDevice(VideoDeviceSaveReqVO updateReqVO) {
        // 校验存在
        validateDeviceExists(updateReqVO.getId());
        validateDeviceCodeUnique(updateReqVO.getId(), updateReqVO.getDeviceId());

        VideoDeviceDO updateObj = VideoDeviceConvert.INSTANCE.convert(updateReqVO);
        videoDeviceMapper.updateById(updateObj);
    }

    @Override
    public void deleteDevice(Long id) {
        validateDeviceExists(id);
        VideoDeviceDO device = videoDeviceMapper.selectById(id);
        if (device != null && device.getDeviceId() != null) {
            try {
                legacyDeviceService.delete(device.getDeviceId());
            } catch (Exception e) {
                log.warn("[deleteDevice] 清理设备底层缓存失败: deviceId={}", device.getDeviceId(), e);
            }
        }
        videoDeviceMapper.deleteById(id);
    }

    @Override
    public void deleteDeviceByCode(String deviceId) {
        VideoDeviceDO device = videoDeviceMapper.selectByDeviceId(deviceId);
        if (device != null) {
            try {
                legacyDeviceService.delete(deviceId);
            } catch (Exception e) {
                log.warn("[deleteDeviceByCode] 清理设备底层缓存失败: deviceId={}", deviceId, e);
            }
            videoDeviceMapper.deleteById(device.getId());
        }
    }

    @Override
    public List<VideoDeviceDO> getDeviceList() {
        return videoDeviceMapper.selectList();
    }

    @Override
    public VideoDeviceDO getDevice(Long id) {
        return videoDeviceMapper.selectById(id);
    }

    @Override
    public VideoDeviceDO getDeviceByDeviceId(String deviceId) {
        return videoDeviceMapper.selectByDeviceId(deviceId);
    }

    @Override
    public PageResult<VideoDeviceDO> getDevicePage(VideoDevicePageReqVO pageReqVO) {
        return videoDeviceMapper.selectPage(pageReqVO, new LambdaQueryWrapperX<VideoDeviceDO>()
                .likeIfPresent(VideoDeviceDO::getDeviceId, pageReqVO.getDeviceId())
                .likeIfPresent(VideoDeviceDO::getName, pageReqVO.getName())
                .eqIfPresent(VideoDeviceDO::getOnLine, pageReqVO.getOnLine())
                .eqIfPresent(VideoDeviceDO::getStreamMode, pageReqVO.getStreamMode())
                .orderByDesc(VideoDeviceDO::getId));
    }

    @Override
    public List<VideoDeviceDO> getOnlineDeviceList() {
        return videoDeviceMapper.selectOnlineList();
    }

    @Override
    public void syncDeviceChannels(String deviceId) {
        Device device = legacyDeviceService.getDeviceByDeviceId(deviceId);
        if (device == null) {
            throw ServiceExceptionUtil.exception(VideoErrorCodeConstants.VIDEO_DEVICE_NOT_EXISTS);
        }
        if (!device.isOnLine()) {
            throw ServiceExceptionUtil.exception(VideoErrorCodeConstants.VIDEO_DEVICE_OFFLINE);
        }
        try {
            log.info("[syncDeviceChannels] 发送 SIP 目录查询信令: deviceId={}", deviceId);
            int sn = (int) ((Math.random() * 9 + 1) * 100000);
            sipCommander.catalogQuery(device, sn, null);
        } catch (Exception e) {
            log.error("[syncDeviceChannels] 发送信令异常: deviceId={}", deviceId, e);
            throw ServiceExceptionUtil.exception(VideoErrorCodeConstants.VIDEO_SIP_SEND_FAILED, e.getMessage());
        }
    }

    private void validateDeviceExists(Long id) {
        if (id == null) {
            throw ServiceExceptionUtil.exception(VideoErrorCodeConstants.VIDEO_DEVICE_NOT_EXISTS);
        }
        VideoDeviceDO device = videoDeviceMapper.selectById(id);
        if (device == null) {
            throw ServiceExceptionUtil.exception(VideoErrorCodeConstants.VIDEO_DEVICE_NOT_EXISTS);
        }
    }

    private void validateDeviceCodeUnique(Long id, String deviceId) {
        VideoDeviceDO device = videoDeviceMapper.selectByDeviceId(deviceId);
        if (device == null) {
            return;
        }
        if (id == null || !device.getId().equals(id)) {
            throw ServiceExceptionUtil.exception(VideoErrorCodeConstants.VIDEO_DEVICE_ID_DUPLICATE);
        }
    }
}
