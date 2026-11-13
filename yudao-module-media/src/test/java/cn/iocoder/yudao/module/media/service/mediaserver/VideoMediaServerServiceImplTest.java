package cn.iocoder.yudao.module.media.service.mediaserver;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.media.controller.admin.mediaserver.vo.VideoMediaServerSaveReqVO;
import cn.iocoder.yudao.module.media.dal.dataobject.mediaserver.VideoMediaServerDO;
import cn.iocoder.yudao.module.media.dal.mysql.mediaserver.VideoMediaServerMapper;
import cn.iocoder.yudao.module.media.enums.VideoErrorCodeConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("流媒体节点服务 - 单元测试")
public class VideoMediaServerServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private VideoMediaServerServiceImpl mediaServerService;

    @Mock
    private VideoMediaServerMapper videoMediaServerMapper;

    @Test
    @DisplayName("测试保存新流媒体节点成功")
    public void testSaveMediaServer_Insert_Success() {
        VideoMediaServerSaveReqVO reqVO = new VideoMediaServerSaveReqVO();
        reqVO.setId("zlm-node-01");
        reqVO.setIp("127.0.0.1");
        reqVO.setHookIp("127.0.0.1");
        reqVO.setHttpPort(80);
        reqVO.setSecret("test-secret");

        when(videoMediaServerMapper.selectById("zlm-node-01")).thenReturn(null);

        mediaServerService.saveMediaServer(reqVO);

        verify(videoMediaServerMapper, times(1)).insert(any(VideoMediaServerDO.class));
    }

    @Test
    @DisplayName("测试更新已存在流媒体节点成功")
    public void testSaveMediaServer_Update_Success() {
        VideoMediaServerSaveReqVO reqVO = new VideoMediaServerSaveReqVO();
        reqVO.setId("zlm-node-01");
        reqVO.setIp("192.168.1.100");
        reqVO.setHookIp("192.168.1.100");
        reqVO.setHttpPort(80);
        reqVO.setSecret("test-secret");

        VideoMediaServerDO exist = new VideoMediaServerDO();
        exist.setId("zlm-node-01");

        when(videoMediaServerMapper.selectById("zlm-node-01")).thenReturn(exist);

        mediaServerService.saveMediaServer(reqVO);

        verify(videoMediaServerMapper, times(1)).updateById(any(VideoMediaServerDO.class));
    }

    @Test
    @DisplayName("测试获取默认流媒体节点成功")
    public void testGetDefaultMediaServer_Success() {
        VideoMediaServerDO defaultServer = new VideoMediaServerDO();
        defaultServer.setId("default-zlm");
        defaultServer.setDefaultServer(1);

        when(videoMediaServerMapper.selectDefaultServer()).thenReturn(defaultServer);

        VideoMediaServerDO result = mediaServerService.getDefaultMediaServer();
        assertNotNull(result);
        assertEquals("default-zlm", result.getId());
    }

    @Test
    @DisplayName("测试获取默认流媒体节点不存在抛出异常")
    public void testGetDefaultMediaServer_NotFound_ThrowsException() {
        when(videoMediaServerMapper.selectDefaultServer()).thenReturn(null);

        ServiceException exception = assertThrows(ServiceException.class, () ->
                mediaServerService.getDefaultMediaServer());

        assertEquals(VideoErrorCodeConstants.VIDEO_MEDIA_SERVER_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("测试更新心跳状态在线")
    public void testUpdateHeartbeat_Success() {
        VideoMediaServerDO server = new VideoMediaServerDO();
        server.setId("zlm-01");
        server.setStatus(0);

        when(videoMediaServerMapper.selectById("zlm-01")).thenReturn(server);

        mediaServerService.updateHeartbeat("zlm-01");

        assertEquals(1, server.getStatus());
        assertNotNull(server.getLastKeepaliveTime());
        verify(videoMediaServerMapper, times(1)).updateById(server);
    }
}
