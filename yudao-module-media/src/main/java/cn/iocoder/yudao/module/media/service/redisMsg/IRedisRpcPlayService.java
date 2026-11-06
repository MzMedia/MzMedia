package cn.iocoder.yudao.module.media.service.redisMsg;

import cn.iocoder.yudao.module.media.common.InviteSessionType;
import cn.iocoder.yudao.module.media.common.StreamInfo;
import cn.iocoder.yudao.module.media.gb28181.bean.RecordInfo;
import cn.iocoder.yudao.module.media.service.bean.DownloadFileInfo;
import cn.iocoder.yudao.module.media.service.bean.ErrorCallback;
import cn.iocoder.yudao.module.media.vmanager.bean.AudioBroadcastResult;

public interface IRedisRpcPlayService {


    void play(String serverId, Integer channelId, ErrorCallback<StreamInfo> callback);

    void stop(String serverId, InviteSessionType type, int channelId, String stream);

    void playback(String serverId, Integer channelId, String startTime, String endTime, ErrorCallback<StreamInfo> callback);

    void playbackPause(String serverId, String streamId);

    void playbackResume(String serverId, String streamId);

    void download(String serverId, Integer channelId, String startTime, String endTime, int downloadSpeed, ErrorCallback<StreamInfo> callback);

    void queryRecordInfo(String serverId, Integer channelId, String startTime, String endTime, ErrorCallback<RecordInfo> callback);

    String frontEndCommand(String serverId, Integer channelId, int cmdCode, int parameter1, int parameter2, int combindCode2);

    void playPush(String serverId, Integer id, ErrorCallback<StreamInfo> callback);

    void playProxy(String serverId, int id, ErrorCallback<StreamInfo> callback);

    void stopProxy(String serverId, int id);

    DownloadFileInfo getRecordPlayUrl(String serverId, Integer recordId);

    AudioBroadcastResult audioBroadcast(String serverId, String deviceId, String channelDeviceId, Boolean broadcastMode);
}
