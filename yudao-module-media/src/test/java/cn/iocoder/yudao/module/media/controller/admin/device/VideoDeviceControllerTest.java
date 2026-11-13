package cn.iocoder.yudao.module.media.controller.admin.device;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.media.controller.admin.device.vo.VideoDevicePageReqVO;
import cn.iocoder.yudao.module.media.controller.admin.device.vo.VideoDeviceRespVO;
import cn.iocoder.yudao.module.media.controller.admin.device.vo.VideoDeviceSaveReqVO;
import cn.iocoder.yudao.module.media.dal.dataobject.device.VideoDeviceDO;
import cn.iocoder.yudao.module.media.service.device.VideoDeviceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("管理后台 - 视频设备 Controller 单元与集成测试")
public class VideoDeviceControllerTest extends BaseMockitoUnitTest {

    @InjectMocks
    private VideoDeviceController videoDeviceController;

    @Mock
    private VideoDeviceService videoDeviceService;

    @Test
    @DisplayName("测试创建视频设备接口")
    public void testCreateDevice_Success() {
        VideoDeviceSaveReqVO reqVO = new VideoDeviceSaveReqVO();
        reqVO.setName("主入口摄像机");
        reqVO.setDeviceId("34020000001320000001");

        when(videoDeviceService.createDevice(reqVO)).thenReturn(1024L);

        CommonResult<Long> result = videoDeviceController.createDevice(reqVO);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals(1024L, result.getData());
        verify(videoDeviceService, times(1)).createDevice(reqVO);
    }

    @Test
    @DisplayName("测试获得设备详情接口")
    public void testGetDevice_Success() {
        Long deviceId = 100L;
        VideoDeviceDO deviceDO = new VideoDeviceDO();
        deviceDO.setId(deviceId);
        deviceDO.setName("南门球机");
        deviceDO.setDeviceId("34020000001320000002");

        when(videoDeviceService.getDevice(deviceId)).thenReturn(deviceDO);

        CommonResult<VideoDeviceRespVO> result = videoDeviceController.getDevice(deviceId);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        assertEquals("南门球机", result.getData().getName());
        assertEquals("34020000001320000002", result.getData().getDeviceId());
    }

    @Test
    @DisplayName("测试删除设备接口")
    public void testDeleteDevice_Success() {
        Long deviceId = 100L;

        CommonResult<Boolean> result = videoDeviceController.deleteDevice(deviceId);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertTrue(result.getData());
        verify(videoDeviceService, times(1)).deleteDevice(deviceId);
    }

    @Test
    @DisplayName("测试同步设备通道接口")
    public void testSyncDeviceChannels_Success() {
        String deviceId = "34020000001320000001";

        CommonResult<Boolean> result = videoDeviceController.syncDeviceChannels(deviceId);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertTrue(result.getData());
        verify(videoDeviceService, times(1)).syncDeviceChannels(deviceId);
    }
}
