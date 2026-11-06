package cn.iocoder.yudao.module.media.service;

import cn.iocoder.yudao.module.media.gb28181.bean.*;
import cn.iocoder.yudao.module.media.media.bean.MediaServer;
import cn.iocoder.yudao.module.media.media.bean.ResultForOnPublish;
import cn.iocoder.yudao.module.media.media.event.hook.HookData;
import cn.iocoder.yudao.module.media.service.bean.ErrorCallback;
import cn.iocoder.yudao.module.media.service.bean.RTPServerParam;
import cn.iocoder.yudao.module.media.service.bean.SSRCInfo;

public interface IReceiveRtpServerService {

    SSRCInfo openGbRTPServer(MediaServer mediaServer, String streamId, String presetSSRC, int tcpMode,
                             boolean playback, boolean ssrcCheck, boolean onlyAuto, boolean disableAuto,
                             ErrorCallback<OpenRTPServerResult> callback);

    SSRCInfo openGbRTPServerForPlay(MediaServer mediaServer, Device device, DeviceChannel channel,
                                    String presetSSRC, boolean record, ErrorCallback<OpenRTPServerResult> callback);

    SSRCInfo openGbRTPServerForPlayback(MediaServer mediaServer, Device device, DeviceChannel channel,
                                        String startTime, String endTime, ErrorCallback<OpenRTPServerResult> callback);

    SSRCInfo openGbRTPServerForDownload(MediaServer mediaServer, Device device, DeviceChannel channel,
                                        String startTime, String endTime, ErrorCallback<OpenRTPServerResult> callback);

    String getPlaybackStream(Device device, DeviceChannel channel, String startTime, String endTime);

    SSRCInfo openGbRTPServerForBroadcast(MediaServer mediaServer, Platform platform, CommonGBChannel channel,
                                         ErrorCallback<OpenRTPServerResult> callback);

    int openCommonRTPServer(RTPServerParam rtpServerParam, ErrorCallback<HookData> callback);

    void closeRTPServer(MediaServer mediaServer, String app, String stream);

    void closeRTPServerByMediaServerId(String mediaServerId, String app, String stream);

    void addAuthenticateInfoForGb28181Talk(MediaServer mediaServer, String streamId);

    void addAuthenticateInfo(String streamId, String streamReplace, Boolean enableAudio, Boolean enableMp4, Integer mp4MaxSecond);

    ResultForOnPublish getAuthenticateInfo(String streamId);

    void refreshAuthenticateInfo(String oldStreamId, String newStreamId);
}
