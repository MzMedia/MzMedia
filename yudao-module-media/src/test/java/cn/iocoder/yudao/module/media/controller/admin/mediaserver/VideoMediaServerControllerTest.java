package cn.iocoder.yudao.module.media.controller.admin.mediaserver;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.media.controller.admin.mediaserver.vo.VideoMediaServerRespVO;
import cn.iocoder.yudao.module.media.controller.admin.mediaserver.vo.VideoMediaServerSaveReqVO;
import cn.iocoder.yudao.module.media.dal.dataobject.mediaserver.VideoMediaServerDO;
import cn.iocoder.yudao.module.media.service.mediaserver.VideoMediaServerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("管理后台 - 流媒体节点 Controller 单元与集成测试")
public class VideoMediaServerControllerTest extends BaseMockitoUnitTest {

    @InjectMocks
    private VideoMediaServerController mediaServerController;

    @Mock
    private VideoMediaServerService videoMediaServerService;

    @Test
    @DisplayName("测试保存流媒体节点接口")
    public void testSaveMediaServer_Success() {
        VideoMediaServerSaveReqVO saveReqVO = new VideoMediaServerSaveReqVO();
        saveReqVO.setId("zlm-test-node");
        saveReqVO.setIp("127.0.0.1");
        saveReqVO.setHttpPort(80);

        CommonResult<Boolean> result = mediaServerController.saveMediaServer(saveReqVO);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertTrue(result.getData());
        verify(videoMediaServerService, times(1)).saveMediaServer(saveReqVO);
    }

    @Test
    @DisplayName("测试获得流媒体节点详情接口")
    public void testGetMediaServer_Success() {
        String nodeId = "zlm-01";
        VideoMediaServerDO mockServer = new VideoMediaServerDO();
        mockServer.setId(nodeId);
        mockServer.setIp("192.168.1.100");
        mockServer.setHttpPort(80);

        when(videoMediaServerService.getMediaServer(nodeId)).thenReturn(mockServer);

        CommonResult<VideoMediaServerRespVO> result = mediaServerController.getMediaServer(nodeId);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        assertEquals(nodeId, result.getData().getId());
        assertEquals("192.168.1.100", result.getData().getIp());
    }

    @Test
    @DisplayName("测试删除流媒体节点接口")
    public void testDeleteMediaServer_Success() {
        String nodeId = "zlm-01";

        CommonResult<Boolean> result = mediaServerController.deleteMediaServer(nodeId);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertTrue(result.getData());
        verify(videoMediaServerService, times(1)).deleteMediaServer(nodeId);
    }
}
