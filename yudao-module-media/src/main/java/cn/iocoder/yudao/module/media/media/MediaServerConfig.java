package cn.iocoder.yudao.module.media.media;

import cn.iocoder.yudao.module.media.conf.MediaConfig;
import cn.iocoder.yudao.module.media.conf.UserSetting;
import cn.iocoder.yudao.module.media.media.bean.MediaServer;
import cn.iocoder.yudao.module.media.media.event.mediaServer.MediaServerChangeEvent;
import cn.iocoder.yudao.module.media.media.service.IMediaServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 启动是从配置文件加载节点信息，以及发送个节点状态管理去控制节点状态
 */
@Slf4j
@Component
public class MediaServerConfig{

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private IMediaServerService mediaServerService;

    @Autowired
    private MediaConfig mediaConfig;

    @Autowired
    private UserSetting userSetting;


    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(){
        // 清理所有在线节点的缓存信息
        mediaServerService.clearMediaServerForOnline();
        MediaServer mediaSerItemInConfig = mediaConfig.buildMediaSer();
        mediaSerItemInConfig.setServerId(userSetting.getServerId());
        mediaServerService.deleteDefault();
        // 发送媒体节点变化事件
        mediaServerService.syncCatchFromDatabase();
        // 获取所有的zlm， 并开启主动连接
        List<MediaServer> all = mediaServerService.getAllFromDatabaseWithOutDefault();
        all.add(mediaSerItemInConfig);
        log.info("[媒体节点] 加载节点列表， 共{}个节点", all.size());
        MediaServerChangeEvent event = new MediaServerChangeEvent(this);
        event.setMediaServerItemList(all);
        applicationEventPublisher.publishEvent(event);
    }
}
