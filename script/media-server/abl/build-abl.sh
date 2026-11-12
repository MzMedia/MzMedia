#!/bin/bash
# ==============================================================================
# ABLMediaServer Docker 镜像构建脚本 (Linux / macOS)
# ==============================================================================
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${SCRIPT_DIR}"

IMAGE_NAME="${1:-ablmediaserver:latest}"
DOWNLOAD_URL="https://github.com/zhichao281/ABLMediaServer/releases/download/2025-10-14/Linux-webrtc.zip"

echo "=========================================================="
echo " 正在准备构建 ABLMediaServer 镜像: ${IMAGE_NAME}"
echo "=========================================================="

# 检查本地是否已有离线包，若无则支持预先下载
if [ ! -f "Linux-webrtc.zip" ] && [ ! -f "ABLMediaServer" ]; then
    echo "💡 本地未检测到 Linux-webrtc.zip，尝试预下载以加速构建..."
    echo "下载地址: ${DOWNLOAD_URL}"
    if curl -fSL --connect-timeout 10 --retry 2 "${DOWNLOAD_URL}" -o "Linux-webrtc.zip"; then
        echo "✅ 下载成功！"
    else
        echo "⚠️  网络下载超时或失败，构建过程中将由 Dockerfile 继续重试..."
        rm -f "Linux-webrtc.zip"
    fi
fi

echo "🚀 开始构建 Docker 镜像..."
docker build -t "${IMAGE_NAME}" -t "mzmedia/ablmediaserver:latest" .

echo "=========================================================="
echo "✅ ABLMediaServer 镜像构建成功: ${IMAGE_NAME}"
echo "可以使用以下命令运行测试:"
echo "  ./run-abl.sh"
echo "或者使用上级目录的 docker-compose:"
echo "  cd .. && docker compose up -d abl"
echo "=========================================================="
