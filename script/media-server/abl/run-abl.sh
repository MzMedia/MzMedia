#!/bin/bash
# ==============================================================================
# ABLMediaServer 单容器快速启动脚本 (Linux / macOS)
# ==============================================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ABL_HTTP_PORT="${ABL_HTTP_PORT:-9000}"
ABL_FLV_PORT="${ABL_FLV_PORT:-9001}"
ABL_WS_PORT="${ABL_WS_PORT:-9002}"
ABL_RTSP_PORT="${ABL_RTSP_PORT:-5544}"
ABL_RTMP_PORT="${ABL_RTMP_PORT:-1936}"
ABL_RTC_PORT="${ABL_RTC_PORT:-9005}"
ABL_RTP_PORT="${ABL_RTP_PORT:-10002}"
ABL_JT_PORT="${ABL_JT_PORT:-7078}"

echo "=========================================================="
echo " 正在启动 ABLMediaServer 流媒体服务器 (Docker 容器模式)..."
echo " HTTP API 端口 : ${ABL_HTTP_PORT}"
echo " HTTP-FLV 端口 : ${ABL_FLV_PORT}"
echo " WS-FLV 端口   : ${ABL_WS_PORT}"
echo " RTSP 端口     : ${ABL_RTSP_PORT}"
echo " RTMP 端口     : ${ABL_RTMP_PORT}"
echo " WebRTC 端口   : ${ABL_RTC_PORT}/udp"
echo " RTP 收流端口  : ${ABL_RTP_PORT}"
echo " JT1078 端口   : ${ABL_JT_PORT}"
echo "=========================================================="

# 确保数据目录存在
mkdir -p "${SCRIPT_DIR}/data/record" "${SCRIPT_DIR}/data/picture"

# 检查镜像是否存在
if ! docker image inspect ablmediaserver:latest >/dev/null 2>&1 && ! docker image inspect mzmedia/ablmediaserver:latest >/dev/null 2>&1; then
    echo "⚠️ 未发现本地 ABLMediaServer 镜像，开始自动构建..."
    "${SCRIPT_DIR}/build-abl.sh"
fi

# 如果容器已存在则先清理
docker rm -f mz-abl >/dev/null 2>&1 || true

docker run -d \
  --name mz-abl \
  --restart unless-stopped \
  --add-host host.docker.internal:host-gateway \
  -p "${ABL_HTTP_PORT}:9000" \
  -p "${ABL_FLV_PORT}:9001" \
  -p "${ABL_WS_PORT}:9002" \
  -p "9003:9003" \
  -p "9004:9004" \
  -p "${ABL_RTSP_PORT}:5544" \
  -p "${ABL_RTMP_PORT}:1936" \
  -p "${ABL_RTP_PORT}:10002" \
  -p "${ABL_JT_PORT}:7078" \
  -p "${ABL_RTC_PORT}:9005/udp" \
  -p "31000-31050:31000-31050/udp" \
  -v "${SCRIPT_DIR}/ABLMediaServer.ini:/opt/abl/ABLMediaServer.ini" \
  -v "${SCRIPT_DIR}/data/record:/opt/abl/record" \
  -v "${SCRIPT_DIR}/data/picture:/opt/abl/picture" \
  ablmediaserver:latest

echo "ABLMediaServer 启动完成！"
echo "检查状态: docker ps -f name=mz-abl"
echo "API 验证: curl http://127.0.0.1:${ABL_HTTP_PORT}/index/api/getServerConfig?secret=035c73f7-bb6b-4889-a715-d9eb2d1925cc"
