package cn.iocoder.yudao.module.media.controller.admin.ptz;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.media.gb28181.bean.Device;
import cn.iocoder.yudao.module.media.gb28181.service.IDeviceService;
import cn.iocoder.yudao.module.media.gb28181.service.IPTZService;
import cn.iocoder.yudao.module.media.gb28181.transmit.cmd.impl.SIPCommander;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("管理后台 - 视频云台控制 Controller 单元测试")
public class VideoPtzControllerTest extends BaseMockitoUnitTest {

    @InjectMocks
    private VideoPtzController ptzController;

    @Mock
    private SIPCommander sipCommander;

    @Mock
    private IDeviceService legacyDeviceService;

    @Mock
    private IPTZService ptzService;

    @Test
    @DisplayName("测试云台方向控制成功")
    public void testPtzControl_Success() {
        String deviceId = "34020000001110000001";
        String channelId = "34020000001320000001";
        Device device = new Device();
        device.setDeviceId(deviceId);

        when(legacyDeviceService.getDeviceByDeviceId(deviceId)).thenReturn(device);

        CommonResult<Boolean> result = ptzController.ptzControl(deviceId, channelId, 1, 50, 50, 50);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertTrue(result.getData());
        verify(ptzService).ptz(device, channelId, 1, 50, 50, 50);
    }

    @Test
    @DisplayName("测试预置位调用成功")
    public void testCallPreset_Success() {
        String deviceId = "34020000001110000001";
        String channelId = "34020000001320000001";
        Device device = new Device();
        device.setDeviceId(deviceId);

        when(legacyDeviceService.getDeviceByDeviceId(deviceId)).thenReturn(device);

        CommonResult<Boolean> result = ptzController.callPreset(deviceId, channelId, 3);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertTrue(result.getData());
        verify(ptzService).frontEndCommand(device, channelId, 129, 0, 3, 0);
    }
}
