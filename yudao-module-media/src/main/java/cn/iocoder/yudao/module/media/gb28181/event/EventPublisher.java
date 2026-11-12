package cn.iocoder.yudao.module.media.gb28181.event;

import cn.iocoder.yudao.module.media.conf.UserSetting;
import cn.iocoder.yudao.module.media.gb28181.bean.CommonGBChannel;
import cn.iocoder.yudao.module.media.gb28181.bean.DeviceAlarmNotify;
import cn.iocoder.yudao.module.media.gb28181.bean.MobilePosition;
import cn.iocoder.yudao.module.media.gb28181.bean.Platform;
import cn.iocoder.yudao.module.media.gb28181.event.alarm.DeviceAlarmEvent;
import cn.iocoder.yudao.module.media.gb28181.event.channel.ChannelEvent;
import cn.iocoder.yudao.module.media.gb28181.event.device.DeviceOfflineEvent;
import cn.iocoder.yudao.module.media.gb28181.event.subscribe.catalog.CatalogEvent;
import cn.iocoder.yudao.module.media.gb28181.event.subscribe.mobilePosition.MobilePositionEvent;
import cn.iocoder.yudao.module.media.media.bean.MediaServer;
import cn.iocoder.yudao.module.media.media.event.mediaServer.MediaServerOfflineEvent;
import cn.iocoder.yudao.module.media.media.event.mediaServer.MediaServerOnlineEvent;
import cn.iocoder.yudao.module.media.service.redisMsg.IRedisRpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @description:Event事件通知推送器，支持推送在线事件、离线事件
 * @author: swwheihei
 * @date:   2020年5月6日 上午11:30:50
 */
@Slf4j
@Component("mediaEventPublisher")
public class EventPublisher {

	@Autowired
    private ApplicationEventPublisher applicationEventPublisher;

	@Autowired
	private UserSetting userSetting;

	@Autowired
	private IRedisRpcService redisRpcService;

	/**
	 * 设备报警事件
	 */
	public void deviceAlarmEventPublish(List<DeviceAlarmNotify> deviceAlarmList) {
		DeviceAlarmEvent alarmEvent = new DeviceAlarmEvent(this);
		alarmEvent.setDeviceAlarmList(deviceAlarmList);
		applicationEventPublisher.publishEvent(alarmEvent);
	}

	public void mediaServerOfflineEventPublish(MediaServer mediaServer){
		MediaServerOfflineEvent outEvent = new MediaServerOfflineEvent(this);
		outEvent.setMediaServer(mediaServer);
		applicationEventPublisher.publishEvent(outEvent);
	}

	public void mediaServerOnlineEventPublish(MediaServer mediaServer) {
		MediaServerOnlineEvent outEvent = new MediaServerOnlineEvent(this);
		outEvent.setMediaServer(mediaServer);
		applicationEventPublisher.publishEvent(outEvent);
	}

	public void channelEventPublish(CommonGBChannel commonGBChannel, ChannelEvent.ChannelEventMessageType type) {
        channelEventPublish(Collections.singletonList(commonGBChannel), type);
	}

	public void channelEventPublishForUpdate(CommonGBChannel commonGBChannel, CommonGBChannel deviceChannelForOld) {
        log.info("[通道改变内部分发-更新] {}", commonGBChannel.getGbDeviceId());
        ChannelEvent channelEvent = ChannelEvent.getInstanceForUpdate(this, Collections.singletonList(commonGBChannel), Collections.singletonList(deviceChannelForOld));
        applicationEventPublisher.publishEvent(channelEvent);
	}

	public void channelEventPublishForUpdate(List<CommonGBChannel> channelList, List<CommonGBChannel> channelListForOld) {
        log.info("[通道改变内部分发-更新] 数量： {}", channelList.size());
        ChannelEvent channelEvent = ChannelEvent.getInstanceForUpdate(this, channelList, channelListForOld);
        applicationEventPublisher.publishEvent(channelEvent);
	}

    public void channelEventPublish(List<CommonGBChannel> channelList, ChannelEvent.ChannelEventMessageType type) {
        log.info("[通道改变内部分发-{}] 数量： {}", type, channelList.size());
		ChannelEvent channelEvent = ChannelEvent.getInstance(this, type, channelList);
		applicationEventPublisher.publishEvent(channelEvent);
	}

	public void catalogEventPublish(Platform platform, CommonGBChannel deviceChannel, String type) {
		catalogEventPublish(platform, Collections.singletonList(deviceChannel), type);
	}
	public void catalogEventPublish(Platform platform, List<CommonGBChannel> deviceChannels, String type) {
		if (platform != null && !userSetting.getServerId().equals(platform.getServerId())) {
			log.info("[国标级联] 目录状态推送， 此上级平台由其他服务处理，消息已经忽略");
			return;
		}
		CatalogEvent outEvent = new CatalogEvent(this);
		List<CommonGBChannel> channels = new ArrayList<>();
		if (deviceChannels.size() > 1) {
			// 数据去重
			Set<String> gbIdSet = new HashSet<>();
			for (CommonGBChannel deviceChannel : deviceChannels) {
				if (deviceChannel != null && deviceChannel.getGbDeviceId() != null && !gbIdSet.contains(deviceChannel.getGbDeviceId())) {
					gbIdSet.add(deviceChannel.getGbDeviceId());
					channels.add(deviceChannel);
				}
			}
		}else {
			channels = deviceChannels;
		}
		outEvent.setChannels(channels);
		outEvent.setType(type);
		if (platform != null) {
			outEvent.setPlatform(platform);
		}
		applicationEventPublisher.publishEvent(outEvent);

	}

	public void mobilePositionEventPublish(MobilePosition mobilePosition) {
		MobilePositionEvent event = new MobilePositionEvent(this);
		event.setMobilePositionList(List.of(mobilePosition));
		applicationEventPublisher.publishEvent(event);
	}

	public void mobilePositionsEventPublish(List<? extends MobilePosition> mobilePositionList) {
		MobilePositionEvent event = new MobilePositionEvent(this);
		event.setMobilePositionList(mobilePositionList);
		applicationEventPublisher.publishEvent(event);
	}


    public void deviceOfflineEventPublish(Set<String> deviceIds) {
		DeviceOfflineEvent event = new DeviceOfflineEvent(this);
		event.setDeviceIds(deviceIds);
		applicationEventPublisher.publishEvent(event);
    }


}
