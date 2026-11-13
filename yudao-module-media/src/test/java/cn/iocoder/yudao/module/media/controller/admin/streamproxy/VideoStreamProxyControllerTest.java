package cn.iocoder.yudao.module.media.controller.admin.streamproxy;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.media.controller.admin.streamproxy.vo.VideoStreamProxyRespVO;
import cn.iocoder.yudao.module.media.controller.admin.streamproxy.vo.VideoStreamProxySaveReqVO;
import cn.iocoder.yudao.module.media.dal.dataobject.stream.VideoStreamProxyDO;
import cn.iocoder.yudao.module.media.service.streamproxy.VideoStreamProxyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("管理后台 - 拉流代理 Controller 单元与集成测试")
public class VideoStreamProxyControllerTest extends BaseMockitoUnitTest {

    @InjectMocks
    private VideoStreamProxyController streamProxyController;

    @Mock
    private VideoStreamProxyService videoStreamProxyService;

    @Test
    @DisplayName("测试创建拉流代理接口")
    public void testCreateStreamProxy_Success() {
        VideoStreamProxySaveReqVO reqVO = new VideoStreamProxySaveReqVO();
        reqVO.setApp("live");
        reqVO.setStream("test01");
        reqVO.setName("车库监控");
        reqVO.setUrl("rtsp://192.168.1.50/live");

        when(videoStreamProxyService.createStreamProxy(reqVO)).thenReturn(55L);

        CommonResult<Long> result = streamProxyController.createStreamProxy(reqVO);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals(55L, result.getData());
        verify(videoStreamProxyService, times(1)).createStreamProxy(reqVO);
    }

    @Test
    @DisplayName("测试获得拉流代理详情接口")
    public void testGetStreamProxy_Success() {
        Long proxyId = 55L;
        VideoStreamProxyDO mockDO = new VideoStreamProxyDO();
        mockDO.setId(proxyId);
        mockDO.setApp("live");
        mockDO.setStream("test01");
        mockDO.setName("车库监控");

        when(videoStreamProxyService.getStreamProxy(proxyId)).thenReturn(mockDO);

        CommonResult<VideoStreamProxyRespVO> result = streamProxyController.getStreamProxy(proxyId);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        assertEquals("车库监控", result.getData().getName());
        assertEquals("test01", result.getData().getStream());
    }

    @Test
    @DisplayName("测试启动拉流代理接口")
    public void testStartStreamProxy_Success() {
        Long proxyId = 55L;

        CommonResult<Boolean> result = streamProxyController.startStreamProxy(proxyId);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertTrue(result.getData());
        verify(videoStreamProxyService, times(1)).startStreamProxy(proxyId);
    }

    @Test
    @DisplayName("测试停止拉流代理接口")
    public void testStopStreamProxy_Success() {
        Long proxyId = 55L;

        CommonResult<Boolean> result = streamProxyController.stopStreamProxy(proxyId);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertTrue(result.getData());
        verify(videoStreamProxyService, times(1)).stopStreamProxy(proxyId);
    }
}
