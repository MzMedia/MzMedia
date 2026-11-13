package cn.iocoder.yudao.module.media.service.device;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.media.controller.admin.device.vo.VideoDevicePageReqVO;
import cn.iocoder.yudao.module.media.controller.admin.device.vo.VideoDeviceSaveReqVO;
import cn.iocoder.yudao.module.media.dal.dataobject.device.VideoDeviceDO;
import cn.iocoder.yudao.module.media.dal.mysql.device.VideoDeviceMapper;
import cn.iocoder.yudao.module.media.enums.VideoErrorCodeConstants;
import cn.iocoder.yudao.module.media.gb28181.service.IDeviceService;
import cn.iocoder.yudao.module.media.gb28181.transmit.cmd.ISIPCommander;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("视频设备服务 - 单元测试")
public class VideoDeviceServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private VideoDeviceServiceImpl videoDeviceService;

    @Mock
    private VideoDeviceMapper videoDeviceMapper;

    @Mock
    private IDeviceService legacyDeviceService;

    @Mock
    private ISIPCommander sipCommander;

    @Test
    @DisplayName("测试创建国标设备成功")
    public void testCreateDevice_Success() {
        VideoDeviceSaveReqVO reqVO = new VideoDeviceSaveReqVO();
        reqVO.setName("主楼东侧枪机");
        reqVO.setDeviceId("34020000001320000001");
        reqVO.setCharset("GB2312");

        when(videoDeviceMapper.selectByDeviceId("34020000001320000001")).thenReturn(null);
        doAnswer(invocation -> {
            VideoDeviceDO arg = invocation.getArgument(0);
            arg.setId(1024L);
            return 1;
        }).when(videoDeviceMapper).insert(any(VideoDeviceDO.class));

        Long deviceId = videoDeviceService.createDevice(reqVO);

        assertNotNull(deviceId);
        assertEquals(1024L, deviceId);
        verify(videoDeviceMapper, times(1)).insert(any(VideoDeviceDO.class));
    }

    @Test
    @DisplayName("测试创建设备时国标编号重复抛出异常")
    public void testCreateDevice_DuplicateCode_ThrowsException() {
        VideoDeviceSaveReqVO reqVO = new VideoDeviceSaveReqVO();
        reqVO.setName("测试设备");
        reqVO.setDeviceId("34020000001320000001");

        VideoDeviceDO existDevice = new VideoDeviceDO();
        existDevice.setId(99L);
        existDevice.setDeviceId("34020000001320000001");

        when(videoDeviceMapper.selectByDeviceId("34020000001320000001")).thenReturn(existDevice);

        ServiceException exception = assertThrows(ServiceException.class, () ->
                videoDeviceService.createDevice(reqVO));

        assertEquals(VideoErrorCodeConstants.VIDEO_DEVICE_ID_DUPLICATE.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("测试删除不存在设备抛出异常")
    public void testDeleteDevice_NotExists_ThrowsException() {
        when(videoDeviceMapper.selectById(999L)).thenReturn(null);

        ServiceException exception = assertThrows(ServiceException.class, () ->
                videoDeviceService.deleteDevice(999L));

        assertEquals(VideoErrorCodeConstants.VIDEO_DEVICE_NOT_EXISTS.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("测试获得设备分页成功")
    public void testGetDevicePage_Success() {
        VideoDevicePageReqVO reqVO = new VideoDevicePageReqVO();
        reqVO.setName("主楼");

        VideoDeviceDO device = new VideoDeviceDO();
        device.setId(1L);
        device.setName("主楼西侧枪机");

        PageResult<VideoDeviceDO> pageResult = new PageResult<>(Collections.singletonList(device), 1L);
        when(videoDeviceMapper.selectPage(eq(reqVO), any())).thenReturn(pageResult);

        PageResult<VideoDeviceDO> result = videoDeviceService.getDevicePage(reqVO);

        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getList().size());
    }
}
