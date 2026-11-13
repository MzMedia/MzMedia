package cn.iocoder.yudao.module.media.service.channel;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.media.controller.admin.channel.vo.VideoChannelPageReqVO;
import cn.iocoder.yudao.module.media.dal.dataobject.channel.VideoChannelDO;
import cn.iocoder.yudao.module.media.dal.mysql.channel.VideoChannelMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DisplayName("视频通道服务 - 单元测试")
public class VideoChannelServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private VideoChannelServiceImpl videoChannelService;

    @Mock
    private VideoChannelMapper videoChannelMapper;

    @Test
    @DisplayName("测试获得通道详情成功")
    public void testGetChannel_Success() {
        VideoChannelDO mockChannel = new VideoChannelDO();
        mockChannel.setId(10L);
        mockChannel.setName("1号通道球机");

        when(videoChannelMapper.selectById(10L)).thenReturn(mockChannel);

        VideoChannelDO channel = videoChannelService.getChannel(10L);
        assertNotNull(channel);
        assertEquals("1号通道球机", channel.getName());
    }

    @Test
    @DisplayName("测试获得通道分页成功")
    public void testGetChannelPage_Success() {
        VideoChannelPageReqVO reqVO = new VideoChannelPageReqVO();
        reqVO.setDeviceId("34020000001320000001");

        VideoChannelDO channel = new VideoChannelDO();
        channel.setId(1L);
        channel.setName("东门通道");

        PageResult<VideoChannelDO> mockPage = new PageResult<>(Collections.singletonList(channel), 1L);
        when(videoChannelMapper.selectPage(eq(reqVO), any())).thenReturn(mockPage);

        PageResult<VideoChannelDO> result = videoChannelService.getChannelPage(reqVO);
        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getList().size());
    }

    @Test
    @DisplayName("测试根据设备国标编号查询通道列表")
    public void testGetChannelListByDeviceId_Success() {
        String deviceId = "34020000001320000001";
        VideoChannelDO channel = new VideoChannelDO();
        channel.setId(2L);
        channel.setDeviceId(deviceId);

        when(videoChannelMapper.selectListByDeviceId(deviceId)).thenReturn(Collections.singletonList(channel));

        List<VideoChannelDO> list = videoChannelService.getChannelListByDeviceId(deviceId);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(deviceId, list.get(0).getDeviceId());
    }
}
