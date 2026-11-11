package cn.iocoder.yudao.module.media.jt1078.codec;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JT/T 1078 传输音视频 RTP 数据包载荷
 *
 * @author MzMedia
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JT1078Message {

    /**
     * 负载类型 (98: H.264, 99: H.265, 8: G.726, 6: AAC 等)
     */
    private int payloadType;

    /**
     * 包序号
     */
    private int sequenceNumber;

    /**
     * 终端手机卡号 (SIM卡号)
     */
    private String simNo;

    /**
     * 逻辑通道号 (1-64)
     */
    private int channelNo;

    /**
     * 数据类型 (0: 视频I帧, 1: 视频P帧, 2: 视频B帧, 3: 音频帧, 4: 透传数据)
     */
    private int dataType;

    /**
     * 分包处理标记 (0: 原子包, 1: 第一包, 2: 最后一包, 3: 中间包)
     */
    private int subPackageType;

    /**
     * 绝对时间戳 (毫秒)
     */
    private long timestamp;

    /**
     * 音视频数据载荷
     */
    private byte[] data;

    public boolean isVideoIFrame() {
        return dataType == 0;
    }

    public boolean isAudio() {
        return dataType == 3;
    }
}
