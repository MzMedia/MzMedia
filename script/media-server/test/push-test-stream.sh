#!/bin/bash
# ==============================================================================
# FFmpeg 模拟摄像头推流测试脚本 (Linux / macOS)
# ==============================================================================

TARGET="${1:-zlm}"

if [ "${TARGET}" == "abl" ]; then
    STREAM_URL="rtmp://127.0.0.1:1936/live/test"
    PLAY_FLV="http://127.0.0.1:9001/live/test.flv"
    PLAY_WS="ws://127.0.0.1:9002/live/test.flv"
    PLAY_RTSP="rtsp://127.0.0.1:5544/live/test"
    NAME="ABLMediaServer"
else
    STREAM_URL="rtmp://127.0.0.1:1935/live/test"
    PLAY_FLV="http://127.0.0.1:18080/live/test.live.flv"
    PLAY_WS="ws://127.0.0.1:18080/live/test.live.flv"
    PLAY_RTSP="rtsp://127.0.0.1:554/live/test"
    NAME="ZLMediaKit"
fi

echo "=========================================================="
echo " 正在向 ${NAME} 推送测试彩条流..."
echo " 推流目标地址: ${STREAM_URL}"
echo "=========================================================="
echo "播流地址参考:"
echo " - HTTP-FLV : ${PLAY_FLV}"
echo " - WS-FLV   : ${PLAY_WS}"
echo " - RTSP     : ${PLAY_RTSP}"
echo "=========================================================="

FFMPEG_CMD="ffmpeg"
if ! command -v ffmpeg &> /dev/null; then
    echo "💡 本机未安装 FFmpeg，尝试通过 Docker 运行临时 FFmpeg 推流容器..."
    docker run --rm -it --net=host jrottenberg/ffmpeg:latest \
        -re -f lavfi -i testsrc=size=1280x720:rate=25 \
        -f lavfi -i sine=frequency=1000:sample_rate=44100 \
        -vcodec libx264 -preset ultrafast -tune zerolatency -b:v 1500k \
        -acodec aac -b:a 128k \
        -f flv "${STREAM_URL}"
else
    ffmpeg -re -f lavfi -i testsrc=size=1280x720:rate=25 \
        -f lavfi -i sine=frequency=1000:sample_rate=44100 \
        -vcodec libx264 -preset ultrafast -tune zerolatency -b:v 1500k \
        -acodec aac -b:a 128k \
        -f flv "${STREAM_URL}"
fi
