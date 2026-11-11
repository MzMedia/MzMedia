package cn.iocoder.yudao.module.media.jt1078.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * JT/T 1078 RTP 报文 Netty 帧解码器
 * 报文头以 0x30 0x31 0x63 0x64 (01cd) 起始
 *
 * @author MzMedia
 */
@Slf4j
public class JT1078FrameDecoder extends ByteToMessageDecoder {

    private static final int HEADER_MAGIC = 0x30316364; // "01cd"
    private static final int MIN_HEADER_LENGTH = 30;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        while (in.readableBytes() >= 4) {
            in.markReaderIndex();
            int magic = in.readInt();
            if (magic == HEADER_MAGIC) {
                // 找到帧头
                if (in.readableBytes() < MIN_HEADER_LENGTH - 4) {
                    in.resetReaderIndex();
                    return;
                }

                // 读取 RTP 基础信息
                byte vpxcc = in.readByte();
                byte mpt = in.readByte();
                int payloadType = mpt & 0x7F;

                int seqNo = in.readUnsignedShort();

                // 6 字节 BCD SIM 卡号
                byte[] simBytes = new byte[6];
                in.readBytes(simBytes);
                String simNo = bcdToString(simBytes);

                int channelNo = in.readUnsignedByte();

                byte typeByte = in.readByte();
                int dataType = (typeByte >> 4) & 0x0F;
                int subPackageType = typeByte & 0x0F;

                // 8 字节时间戳
                long timestamp = in.readLong();

                // 针对音视频区分是否有时间间隔字段 (根据 1078 规范，视频帧有 2 或 4 字节的 interval)
                if (dataType <= 2) { // 视频帧含有时间间隔
                    if (in.readableBytes() < 4) {
                        in.resetReaderIndex();
                        return;
                    }
                    in.skipBytes(4); // 上一帧时间间隔与上一I帧时间间隔
                }

                if (in.readableBytes() < 2) {
                    in.resetReaderIndex();
                    return;
                }
                int bodyLength = in.readUnsignedShort();

                if (in.readableBytes() < bodyLength) {
                    in.resetReaderIndex();
                    return;
                }

                byte[] data = new byte[bodyLength];
                in.readBytes(data);

                JT1078Message msg = JT1078Message.builder()
                        .payloadType(payloadType)
                        .sequenceNumber(seqNo)
                        .simNo(simNo)
                        .channelNo(channelNo)
                        .dataType(dataType)
                        .subPackageType(subPackageType)
                        .timestamp(timestamp)
                        .data(data)
                        .build();

                out.add(msg);
            } else {
                // 滑动窗口寻找下一个 magic
                in.resetReaderIndex();
                in.readByte();
            }
        }
    }

    private static String bcdToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append((byte) ((b >> 4) & 0x0F));
            sb.append((byte) (b & 0x0F));
        }
        return sb.toString();
    }
}
