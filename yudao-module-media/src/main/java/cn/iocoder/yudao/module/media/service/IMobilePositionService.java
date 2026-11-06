package cn.iocoder.yudao.module.media.service;


import cn.iocoder.yudao.module.media.gb28181.bean.MobilePosition;
import cn.iocoder.yudao.module.media.gb28181.bean.Platform;

import java.util.List;

public interface IMobilePositionService {

    List<MobilePosition> queryMobilePositions(Integer channelId, String startTime, String endTime);

    List<Platform> queryEnablePlatformListWithAsMessageChannel();

    MobilePosition queryLatestPosition(Integer channelId);

}
