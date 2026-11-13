package cn.iocoder.yudao.module.media.service.device;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.media.controller.admin.device.vo.VideoDevicePageReqVO;
import cn.iocoder.yudao.module.media.controller.admin.device.vo.VideoDeviceSaveReqVO;
import cn.iocoder.yudao.module.media.dal.dataobject.device.VideoDeviceDO;

import java.util.List;

/**
 * 视频设备服务接口
 *
 * @author MzMedia Architect
 */
public interface VideoDeviceService {

    /**
     * 创建视频接入设备
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createDevice(VideoDeviceSaveReqVO createReqVO);

    /**
     * 更新视频接入设备
     *
     * @param updateReqVO 更新信息
     */
    void updateDevice(VideoDeviceSaveReqVO updateReqVO);

    /**
     * 删除视频接入设备
     *
     * @param id 编号
     */
    void deleteDevice(Long id);

    /**
     * 根据设备国标编号删除视频接入设备
     *
     * @param deviceId 设备国标编号
     */
    void deleteDeviceByCode(String deviceId);

    /**
     * 获得所有视频设备列表
     *
     * @return 视频设备列表
     */
    List<VideoDeviceDO> getDeviceList();

    /**
     * 获得视频设备
     *
     * @param id 编号
     * @return 视频设备
     */
    VideoDeviceDO getDevice(Long id);

    /**
     * 根据设备国标编号获得视频设备
     *
     * @param deviceId 国标编号
     * @return 视频设备
     */
    VideoDeviceDO getDeviceByDeviceId(String deviceId);

    /**
     * 获得视频设备分页
     *
     * @param pageReqVO 分页查询
     * @return 视频设备分页
     */
    PageResult<VideoDeviceDO> getDevicePage(VideoDevicePageReqVO pageReqVO);

    /**
     * 获得在线设备列表
     *
     * @return 在线设备列表
     */
    List<VideoDeviceDO> getOnlineDeviceList();

    /**
     * 同步设备通道目录 (发送 SIP Catalog 信令)
     *
     * @param deviceId 设备国标编号
     */
    void syncDeviceChannels(String deviceId);
}
