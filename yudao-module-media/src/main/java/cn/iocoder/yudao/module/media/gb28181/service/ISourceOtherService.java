package cn.iocoder.yudao.module.media.gb28181.service;

import cn.iocoder.yudao.module.media.gb28181.bean.MobilePosition;

import java.util.List;

/**
 * 资源能力接入-其他
 */
public interface ISourceOtherService {


    Boolean closeStreamOnNoneReader(String mediaServerId, String app, String stream, String schema);

    Boolean addChannelIdForMobilePosition(List<? extends MobilePosition> mobilePositionList);

}
