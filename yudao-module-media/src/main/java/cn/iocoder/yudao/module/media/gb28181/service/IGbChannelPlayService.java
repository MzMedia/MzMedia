package cn.iocoder.yudao.module.media.gb28181.service;

import cn.iocoder.yudao.module.media.common.InviteSessionType;
import cn.iocoder.yudao.module.media.common.StreamInfo;
import cn.iocoder.yudao.module.media.gb28181.bean.CommonGBChannel;
import cn.iocoder.yudao.module.media.gb28181.bean.CommonRecordInfo;
import cn.iocoder.yudao.module.media.gb28181.bean.InviteMessageInfo;
import cn.iocoder.yudao.module.media.gb28181.bean.Platform;
import cn.iocoder.yudao.module.media.service.bean.ErrorCallback;
import cn.iocoder.yudao.module.media.vmanager.bean.AudioTalkResult;

import java.util.List;

public interface IGbChannelPlayService {

    void startInvite(CommonGBChannel channel, InviteMessageInfo inviteInfo, Platform platform, ErrorCallback<StreamInfo> callback);

    void stopInvite(InviteSessionType type, CommonGBChannel channel, String stream);

    void playback(CommonGBChannel channel, Long startTime, Long stopTime, ErrorCallback<StreamInfo> callback);

    void download(CommonGBChannel channel, Long startTime, Long stopTime, Integer downloadSpeed,
                  ErrorCallback<StreamInfo> callback);

    void stopPlay(CommonGBChannel channel);

    void play(CommonGBChannel channel, Platform platform, Boolean record, ErrorCallback<StreamInfo> callback);

    void stopPlayback(CommonGBChannel channel, String stream);

    void stopDownload(CommonGBChannel channel, String stream);

    void playbackPause(CommonGBChannel channel, String streamId);

    void playbackResume(CommonGBChannel channel, String streamId);

    void playbackSeek(CommonGBChannel channel, String stream, long seekTime);

    void playbackSpeed(CommonGBChannel channel, String stream, Double speed);

    void queryRecord(CommonGBChannel channel, String startTime, String endTime, ErrorCallback<List<CommonRecordInfo>> callback);


    void getSnap(CommonGBChannel channel, ErrorCallback<byte[]> callback);

    AudioTalkResult startTalk(CommonGBChannel channel);

    void stopTalk(CommonGBChannel channel);

    AudioTalkResult startBroadcast(CommonGBChannel channel);

    void stopBroadcast(CommonGBChannel channel);
}
