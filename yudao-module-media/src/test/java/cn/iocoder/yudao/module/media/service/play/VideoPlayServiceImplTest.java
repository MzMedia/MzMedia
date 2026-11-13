package cn.iocoder.yudao.module.media.service.play;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.media.controller.admin.play.vo.VideoPlayReqVO;
import cn.iocoder.yudao.module.media.controller.admin.play.vo.VideoPlayRespVO;
import cn.iocoder.yudao.module.media.dal.dataobject.channel.VideoChannelDO;
import cn.iocoder.yudao.module.media.dal.mysql.channel.VideoChannelMapper;
import cn.iocoder.yudao.module.media.enums.VideoDeviceTypeEnum;
import cn.iocoder.yudao.module.media.enums.VideoErrorCodeConstants;
import cn.iocoder.yudao.module.media.service.play.strategy.VideoPlayStrategy;
import cn.iocoder.yudao.module.media.service.play.strategy.VideoPlayStrategyFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("视频点播统一门面服务 - 单元测试")
public class VideoPlayServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private VideoPlayServiceImpl videoPlayService;

    @Mock
    private VideoChannelMapper videoChannelMapper;

    @Mock
    private VideoPlayStrategyFactory playStrategyFactory;

    @Mock
    private VideoPlayStrategy mockPlayStrategy;

    @Test
    @DisplayName("测试通过 channelId 发起视频点播成功")
    public void testStartPlay_ByChannelId_Success() {
        // 1. 准备数据
        Long channelId = 100L;
        VideoChannelDO mockChannel = new VideoChannelDO();
        mockChannel.setId(channelId);
        mockChannel.setDeviceId("34020000001320000001");
        mockChannel.setChannelId("34020000001310000001");
        mockChannel.setDataType(VideoDeviceTypeEnum.GB28181.getCode());

        VideoPlayReqVO reqVO = new VideoPlayReqVO();
        reqVO.setChannelId(channelId);

        VideoPlayRespVO mockResp = new VideoPlayRespVO();
        mockResp.setStreamId("live_test_stream");
        mockResp.setFlv("http://127.0.0.1:80/live/live_test_stream.flv");

        when(videoChannelMapper.selectById(channelId)).thenReturn(mockChannel);
        when(playStrategyFactory.getStrategy(VideoDeviceTypeEnum.GB28181)).thenReturn(mockPlayStrategy);
        when(mockPlayStrategy.startPlay(mockChannel)).thenReturn(mockResp);

        // 2. 执行调用
        VideoPlayRespVO respVO = videoPlayService.startPlay(reqVO);

        // 3. 断言验证
        assertNotNull(respVO);
        assertEquals("live_test_stream", respVO.getStreamId());
        assertEquals("http://127.0.0.1:80/live/live_test_stream.flv", respVO.getFlv());
        verify(mockPlayStrategy, times(1)).startPlay(mockChannel);
    }

    @Test
    @DisplayName("测试通道不存在且未提供国标编号时抛出异常")
    public void testStartPlay_ChannelNotExists_ThrowsException() {
        VideoPlayReqVO reqVO = new VideoPlayReqVO();
        reqVO.setChannelId(999L);

        when(videoChannelMapper.selectById(999L)).thenReturn(null);

        ServiceException exception = assertThrows(ServiceException.class, () ->
                videoPlayService.startPlay(reqVO));

        assertEquals(VideoErrorCodeConstants.VIDEO_CHANNEL_NOT_EXISTS.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("测试停止视频点播成功")
    public void testStopPlay_Success() {
        Long channelId = 100L;
        VideoChannelDO mockChannel = new VideoChannelDO();
        mockChannel.setId(channelId);
        mockChannel.setDataType(VideoDeviceTypeEnum.GB28181.getCode());

        when(videoChannelMapper.selectById(channelId)).thenReturn(mockChannel);
        when(playStrategyFactory.getStrategy(VideoDeviceTypeEnum.GB28181)).thenReturn(mockPlayStrategy);

        videoPlayService.stopPlay(channelId);

        verify(mockPlayStrategy, times(1)).stopPlay(mockChannel);
    }
}
