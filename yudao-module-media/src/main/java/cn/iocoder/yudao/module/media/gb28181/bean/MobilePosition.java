package cn.iocoder.yudao.module.media.gb28181.bean;

import cn.iocoder.yudao.module.media.gb28181.utils.NumericUtil;
import cn.iocoder.yudao.module.media.gb28181.utils.SipUtils;
import cn.iocoder.yudao.module.media.utils.DateUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Element;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.module.media.gb28181.utils.XmlUtil.getText;

/**
 * @description: 移动位置bean
 * @author: lawrencehj
 * @date: 2021年1月23日
 */

@Slf4j
@Data
public class MobilePosition {

    /**
     * 通道数据库自增Id
     */
    private Integer channelId;

    /**
     * 通道国标编号
     */
    private String channelDeviceId;

    /**
     * 通知时间
     */
    private long timestamp;

    /**
     * 经度
     */
    private double longitude;

    /**
     * 纬度
     */
    private double latitude;

    /**
     * 海拔高度
     */
    private double altitude;

    /**
     * 速度
     */
    private double speed;

    /**
     * 方向
     */
    private double direction;

    /**
     * 创建时间
     */
    private String createTime;

    public static List<MobilePosition> decode(Element rootElementAfterCharset) {

        List<MobilePosition> mobilePositions = new ArrayList<>();

        MobilePosition mobilePosition = new MobilePosition();
        mobilePosition.setCreateTime(DateUtil.getNow());

        String channelId = getText(rootElementAfterCharset, "DeviceID");

        mobilePosition.setChannelDeviceId(channelId);
        String time = getText(rootElementAfterCharset, "Time");
        if (ObjectUtils.isEmpty(time)){
            mobilePosition.setTimestamp(System.currentTimeMillis());
        }else {
            Long timestamp = SipUtils.parseTimeForTimestamp(time);
            if(timestamp == null) {
                log.warn("解析移动位置时间失败：{}， 使用当前时间", time);
                mobilePosition.setTimestamp(System.currentTimeMillis());
            }else {
                mobilePosition.setTimestamp(timestamp);
            }
        }
        mobilePosition.setLongitude(Double.parseDouble(getText(rootElementAfterCharset, "Longitude")));
        mobilePosition.setLatitude(Double.parseDouble(getText(rootElementAfterCharset, "Latitude")));
        if (NumericUtil.isDouble(getText(rootElementAfterCharset, "Speed"))) {
            mobilePosition.setSpeed(Double.parseDouble(getText(rootElementAfterCharset, "Speed")));
        } else {
            mobilePosition.setSpeed(0.0);
        }
        if (NumericUtil.isDouble(getText(rootElementAfterCharset, "Direction"))) {
            mobilePosition.setDirection(Double.parseDouble(getText(rootElementAfterCharset, "Direction")));
        } else {
            mobilePosition.setDirection(0.0);
        }
        if (NumericUtil.isDouble(getText(rootElementAfterCharset, "Altitude"))) {
            mobilePosition.setAltitude(Double.parseDouble(getText(rootElementAfterCharset, "Altitude")));
        } else {
            mobilePosition.setAltitude(0.0);
        }

        mobilePositions.add(mobilePosition);

        return mobilePositions;
    }

    @Override
    public String toString() {
        return "MobilePosition{" +
                ", channelId=" + channelId +
                ", channelDeviceId='" + channelDeviceId + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", altitude=" + altitude +
                ", speed=" + speed +
                ", direction=" + direction +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
