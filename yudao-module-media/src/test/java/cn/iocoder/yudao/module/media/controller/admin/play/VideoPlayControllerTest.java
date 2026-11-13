package cn.iocoder.yudao.module.media.controller.admin.play;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.media.controller.admin.play.vo.VideoPlayReqVO;
import cn.iocoder.yudao.module.media.controller.admin.play.vo.VideoPlayRespVO;
import cn.iocoder.yudao.module.media.service.play.VideoPlayService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("管理后台 - 视频点播 Controller 单元与集成测试")
public class VideoPlayControllerTest extends BaseMockitoUnitTest {

    @InjectMocks
    private VideoPlayController videoPlayController;

    @Mock
    private VideoPlayService videoPlayService;

    @Test
    @DisplayName("测试发起实时点播拉流接口")
    public void testStartPlay_Success() {
        VideoPlayReqVO reqVO = new VideoPlayReqVO();
        reqVO.setChannelId(101L);

        VideoPlayRespVO mockResp = new VideoPlayRespVO();
        mockResp.setStreamId("stream_live_101");
        mockResp.setFlv("http://127.0.0.1:80/live/stream_live_101.flv");
        mockResp.setWsFlv("ws://127.0.0.1:80/live/stream_live_101.flv");
        mockResp.setHls("http://127.0.0.1:80/live/stream_live_101/hls.m3u8");
        mockResp.setWebrtc("webrtc://127.0.0.1:80/live/stream_live_101");

        when(videoPlayService.startPlay(reqVO)).thenReturn(mockResp);

        CommonResult<VideoPlayRespVO> result = videoPlayController.startPlay(reqVO);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        assertEquals("stream_live_101", result.getData().getStreamId());
        assertEquals("http://127.0.0.1:80/live/stream_live_101.flv", result.getData().getFlv());
        verify(videoPlayService, times(1)).startPlay(reqVO);
    }

    @Test
    @DisplayName("测试停止实时点播接口")
    public void testStopPlay_Success() {
        Long channelId = 101L;

        CommonResult<Boolean> result = videoPlayController.stopPlay(channelId);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertTrue(result.getData());
        verify(videoPlayService, times(1)).stopPlay(channelId);
    }
}
