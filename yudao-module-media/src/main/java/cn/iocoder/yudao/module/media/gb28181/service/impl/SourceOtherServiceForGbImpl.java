package cn.iocoder.yudao.module.media.gb28181.service.impl;

import cn.iocoder.yudao.module.media.common.InviteInfo;
import cn.iocoder.yudao.module.media.common.InviteSessionStatus;
import cn.iocoder.yudao.module.media.common.InviteSessionType;
import cn.iocoder.yudao.module.media.common.enums.ChannelDataType;
import cn.iocoder.yudao.module.media.common.enums.MediaStreamUtil;
import cn.iocoder.yudao.module.media.conf.UserSetting;
import cn.iocoder.yudao.module.media.gb28181.bean.Device;
import cn.iocoder.yudao.module.media.gb28181.bean.DeviceChannel;
import cn.iocoder.yudao.module.media.gb28181.bean.DeviceMobilePosition;
import cn.iocoder.yudao.module.media.gb28181.bean.MobilePosition;
import cn.iocoder.yudao.module.media.gb28181.service.IDeviceChannelService;
import cn.iocoder.yudao.module.media.gb28181.service.IInviteStreamService;
import cn.iocoder.yudao.module.media.gb28181.service.ISourceOtherService;
import cn.iocoder.yudao.module.media.gb28181.utils.Coordtransform;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service(ChannelDataType.OTHER_SERVICE + ChannelDataType.GB28181)
@RequiredArgsConstructor
public class SourceOtherServiceForGbImpl implements ISourceOtherService {


    private final IInviteStreamService inviteStreamService;

    private final IDeviceChannelService deviceChannelService;

    private final UserSetting userSetting;

    @Override
    public Boolean closeStreamOnNoneReader(String mediaServerId, String app, String stream, String schema) {
        if (MediaStreamUtil.GB28181_TALK.equals(app) ||  MediaStreamUtil.GB28181_BROADCAST.equals(app)) {
            // 国标对讲/广播流， 直接关闭
            return false;
        }
        if (!MediaStreamUtil.isGB28181(app, stream)) {
            return null;
        }
        // 国标流， 点播/录像回放/录像下载
        // 保留中的下载会话与回放共用同一流名，优先取点播/回放会话
        InviteInfo inviteInfo = inviteStreamService.getInviteInfoByStreamPreferPlay(stream);
        if (inviteInfo == null) {
            return null;
        }
        if (inviteInfo.getStatus() == InviteSessionStatus.ok) {
            // 录像下载
            if (inviteInfo.getType() == InviteSessionType.DOWNLOAD) {
                return false;
            }
            DeviceChannel deviceChannel = deviceChannelService.getOneForSourceById(inviteInfo.getChannelId());
            if (deviceChannel == null) {
                return false;
            }
        }
        return userSetting.getStreamOnDemand();
    }

    @Override
    public Boolean addChannelIdForMobilePosition(List<? extends MobilePosition> mobilePositionList) {
        if (CollectionUtils.isEmpty(mobilePositionList)) {
            return false;
        }
        if (!(mobilePositionList.get(0) instanceof DeviceMobilePosition)) {
            return null;
        }
        List<DeviceMobilePosition> deviceMobilePositionList = mobilePositionList.stream()
                .map(DeviceMobilePosition.class::cast)
                .collect(Collectors.toList());

        Map<String, DeviceChannel> deviceChannelMap = deviceChannelService.getAllForMobilePosition(deviceMobilePositionList);
        if (CollectionUtils.isEmpty(deviceChannelMap)) {
            return false;
        }

        // 查询通道表，为mobilePositionList赋值channelId
        for (DeviceMobilePosition position : deviceMobilePositionList) {
            if (position.getDevice() == null) {
                continue;
            }
            String key = position.getDevice().getId() + "_" + position.getChannelDeviceId();
            DeviceChannel deviceChannel = deviceChannelMap.get(key);
            Device device = position.getDevice();
            if (deviceChannel != null) {
                position.setChannelId(deviceChannel.getId());

                if (device.getGeoCoordSys().equalsIgnoreCase("GCJ02")) {
                    Double[] wgs84Position = Coordtransform.GCJ02ToWGS84(position.getLongitude(), position.getLatitude());
                    position.setLongitude(wgs84Position[0]);
                    position.setLatitude(wgs84Position[1]);
                }
                deviceChannel.setLongitude(position.getLongitude());
                deviceChannel.setLatitude(position.getLatitude());
            }
        }
        // 批量更新通道
        deviceChannelService.asyncBatchChannelPosition(deviceChannelMap.values());
        return true;
    }
}
