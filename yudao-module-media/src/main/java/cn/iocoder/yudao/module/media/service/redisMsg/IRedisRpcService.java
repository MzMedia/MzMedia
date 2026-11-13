package cn.iocoder.yudao.module.media.service.redisMsg;

import cn.iocoder.yudao.module.media.common.CommonCallback;
import cn.iocoder.yudao.module.media.common.StreamInfo;
import cn.iocoder.yudao.module.media.gb28181.bean.*;
import cn.iocoder.yudao.module.media.gb28181.controller.bean.ChannelListForRpcParam;
import cn.iocoder.yudao.module.media.gb28181.event.subscribe.catalog.CatalogEvent;
import cn.iocoder.yudao.module.media.vmanager.bean.MzResult;

import java.util.List;

public interface IRedisRpcService {

    SendRtpInfo getSendRtpItem(String callId);

    MzResult startSendRtp(String callId, SendRtpInfo sendRtpItem);

    MzResult stopSendRtp(String callId);

    long waitePushStreamOnline(SendRtpInfo sendRtpItem, CommonCallback<Integer> callback);

    void stopWaitePushStreamOnline(SendRtpInfo sendRtpItem);

    void rtpSendStopped(String callId);

    void removeCallback(long key);

    long onStreamOnlineEvent(String app, String stream, CommonCallback<StreamInfo> callback);

    void unPushStreamOnlineEvent(String app, String stream);

    void subscribeCatalog(int id, int cycle);

    void subscribeMobilePosition(int id, int cycle, int interval);

    boolean updatePlatform(String serverId, Platform platform);

    boolean deletePlatform(String serverId, Integer platformId);

    int addPlatformChannelList(String serverGBId, ChannelListForRpcParam channelListForRpcParam);

    int removeAllPlatformChannel(String serverId, Integer platformId);

    int removePlatformChannelList(String serverId, ChannelListForRpcParam channelListForRpcParam);

    boolean updateCustomPlatformChannel(String serverId, PlatformChannel channel);

    boolean pushPlatformChannel(String serverId, Integer platformId);

    void catalogEventPublish(String serverId, CatalogEvent catalogEvent);

    MzResult<SyncStatus> devicesSync(String serverId, String deviceId);

    SyncStatus getChannelSyncStatus(String serverId, String deviceId);

    MzResult<String> deviceBasicConfig(String serverId, Device device, BasicParam basicParam);

    MzResult<String> deviceVideoParamConfig(String serverId, Device device, VideoParamOpt videoParamOpt);

    MzResult<Object> deviceConfigQuery(String serverId, Device device, String channelId, String configType);

    void teleboot(String serverId, Device device);

    MzResult<String> recordControl(String serverId, Device device, String channelId, String recordCmdStr);

    MzResult<String> guard(String serverId, Device device, String guardCmdStr);

    MzResult<String> resetAlarm(String serverId, Device device, String channelId, String alarmMethod, String alarmType);

    void iFrame(String serverId, Device device, String channelId);

    MzResult<String> homePosition(String serverId, Device device, String channelId, Boolean enabled, Integer resetTime, Integer presetIndex);

    void dragZoomIn(String serverId, Device device, String channelId, int length, int width, int midPointX, int midPointY, int lengthX, int lengthY);

    void dragZoomOut(String serverId, Device device, String channelId, int length, int width, int midPointX, int midPointY, int lengthX, int lengthY);

    MzResult<String> deviceStatus(String serverId, Device device);

    MzResult<String> alarm(String serverId, Device device, String startPriority, String endPriority, String alarmMethod, String alarmType, String startTime, String endTime);

    MzResult<Object> deviceInfo(String serverId, Device device);

    MzResult<List<Preset>> queryPreset(String serverId, Device device, String channelId);

    void subscribeAlarm(int id, int cycle);
}
