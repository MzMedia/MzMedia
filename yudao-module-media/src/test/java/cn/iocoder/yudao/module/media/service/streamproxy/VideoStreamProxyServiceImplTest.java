package cn.iocoder.yudao.module.media.service.streamproxy;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.media.controller.admin.streamproxy.vo.VideoStreamProxySaveReqVO;
import cn.iocoder.yudao.module.media.dal.dataobject.stream.VideoStreamProxyDO;
import cn.iocoder.yudao.module.media.dal.mysql.stream.VideoStreamProxyMapper;
import cn.iocoder.yudao.module.media.enums.VideoErrorCodeConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("视频拉流代理服务 - 单元测试")
public class VideoStreamProxyServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private VideoStreamProxyServiceImpl streamProxyService;

    @Mock
    private VideoStreamProxyMapper videoStreamProxyMapper;

    @Test
    @DisplayName("测试创建拉流代理成功")
    public void testCreateStreamProxy_Success() {
        VideoStreamProxySaveReqVO reqVO = new VideoStreamProxySaveReqVO();
        reqVO.setApp("live");
        reqVO.setStream("test_stream");
        reqVO.setName("测试RTSP拉流");
        reqVO.setUrl("rtsp://192.168.1.100:554/live");

        when(videoStreamProxyMapper.selectByAppAndStream("live", "test_stream")).thenReturn(null);
        doAnswer(invocation -> {
            VideoStreamProxyDO proxyDO = invocation.getArgument(0);
            proxyDO.setId(88L);
            return 1;
        }).when(videoStreamProxyMapper).insert(any(VideoStreamProxyDO.class));

        Long id = streamProxyService.createStreamProxy(reqVO);
        assertNotNull(id);
        assertEquals(88L, id);
        verify(videoStreamProxyMapper, times(1)).insert(any(VideoStreamProxyDO.class));
    }

    @Test
    @DisplayName("测试创建拉流代理时流重复抛出异常")
    public void testCreateStreamProxy_Duplicate_ThrowsException() {
        VideoStreamProxySaveReqVO reqVO = new VideoStreamProxySaveReqVO();
        reqVO.setApp("live");
        reqVO.setStream("test_stream");

        VideoStreamProxyDO exist = new VideoStreamProxyDO();
        exist.setId(1L);

        when(videoStreamProxyMapper.selectByAppAndStream("live", "test_stream")).thenReturn(exist);

        assertThrows(ServiceException.class, () ->
                streamProxyService.createStreamProxy(reqVO));
    }

    @Test
    @DisplayName("测试启动拉流代理成功")
    public void testStartStreamProxy_Success() {
        VideoStreamProxyDO mockProxy = new VideoStreamProxyDO();
        mockProxy.setId(5L);
        mockProxy.setApp("live");
        mockProxy.setStream("stream01");
        mockProxy.setUrl("rtmp://127.0.0.1/live/01");
        mockProxy.setEnable(0);

        when(videoStreamProxyMapper.selectById(5L)).thenReturn(mockProxy);

        streamProxyService.startStreamProxy(5L);

        assertEquals(1, mockProxy.getEnable());
        verify(videoStreamProxyMapper, times(1)).updateById(mockProxy);
    }

    @Test
    @DisplayName("测试停止拉流代理成功")
    public void testStopStreamProxy_Success() {
        VideoStreamProxyDO mockProxy = new VideoStreamProxyDO();
        mockProxy.setId(5L);
        mockProxy.setEnable(1);

        when(videoStreamProxyMapper.selectById(5L)).thenReturn(mockProxy);

        streamProxyService.stopStreamProxy(5L);

        assertEquals(0, mockProxy.getEnable());
        verify(videoStreamProxyMapper, times(1)).updateById(mockProxy);
    }
}
