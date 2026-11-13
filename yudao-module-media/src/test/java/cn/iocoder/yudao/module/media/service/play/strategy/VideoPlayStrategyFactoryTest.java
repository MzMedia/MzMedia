package cn.iocoder.yudao.module.media.service.play.strategy;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.media.enums.VideoDeviceTypeEnum;
import cn.iocoder.yudao.module.media.enums.VideoErrorCodeConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DisplayName("视频点播策略工厂 - 单元测试")
public class VideoPlayStrategyFactoryTest extends BaseMockitoUnitTest {

    private VideoPlayStrategyFactory strategyFactory;

    @Mock
    private Gb28181PlayStrategy gb28181PlayStrategy;

    @Mock
    private StreamProxyPlayStrategy streamProxyPlayStrategy;

    @BeforeEach
    public void setUp() {
        when(gb28181PlayStrategy.getSupportType()).thenReturn(VideoDeviceTypeEnum.GB28181);
        when(streamProxyPlayStrategy.getSupportType()).thenReturn(VideoDeviceTypeEnum.STREAM_PROXY);

        strategyFactory = new VideoPlayStrategyFactory(Arrays.asList(gb28181PlayStrategy, streamProxyPlayStrategy));
    }

    @Test
    @DisplayName("测试获取国标GB28181点播策略成功")
    public void testGetStrategy_Gb28181_Success() {
        VideoPlayStrategy strategy = strategyFactory.getStrategy(VideoDeviceTypeEnum.GB28181);
        assertNotNull(strategy);
        assertEquals(gb28181PlayStrategy, strategy);
    }

    @Test
    @DisplayName("测试获取拉流代理点播策略成功")
    public void testGetStrategy_StreamProxy_Success() {
        VideoPlayStrategy strategy = strategyFactory.getStrategy(VideoDeviceTypeEnum.STREAM_PROXY);
        assertNotNull(strategy);
        assertEquals(streamProxyPlayStrategy, strategy);
    }

    @Test
    @DisplayName("测试未知设备类型抛出不支持的流媒体协议异常")
    public void testGetStrategy_UnsupportedProtocol_ThrowsException() {
        ServiceException exception = assertThrows(ServiceException.class, () ->
                strategyFactory.getStrategy(VideoDeviceTypeEnum.STREAM_PUSH));

        assertEquals(VideoErrorCodeConstants.VIDEO_UNSUPPORTED_PROTOCOL.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("测试null类型默认回退至GB28181策略")
    public void testGetStrategy_NullProtocol_FallbackGb28181() {
        VideoPlayStrategy strategy = strategyFactory.getStrategy(null);
        assertNotNull(strategy);
        assertEquals(gb28181PlayStrategy, strategy);
    }
}
