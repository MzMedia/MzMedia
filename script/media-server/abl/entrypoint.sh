#!/bin/bash
set -e

# ==============================================================================
# ABLMediaServer Docker Entrypoint
# ==============================================================================

WORK_DIR="/opt/abl"
cd "${WORK_DIR}"

export LD_LIBRARY_PATH="${WORK_DIR}:${LD_LIBRARY_PATH}"

# 确保目录和权限
mkdir -p "${WORK_DIR}/record" "${WORK_DIR}/picture"
chmod -R 755 "${WORK_DIR}"
if [ -f "${WORK_DIR}/ABLMediaServer" ]; then
    chmod +x "${WORK_DIR}/ABLMediaServer"
else
    echo "❌ 错误: 未在 ${WORK_DIR} 找到 ABLMediaServer 可执行程序！"
    echo "请确认是否已将 Linux-webrtc.zip 解压或构建镜像时下载完成。"
    exit 1
fi

INI_FILE="${WORK_DIR}/ABLMediaServer.ini"

# 如果配置了 SERVER_IP，动态更新 ini
if [ -n "${SERVER_IP}" ] && [ -f "${INI_FILE}" ]; then
    echo "🔧 动态更新 ServerIP 与 localipAddress 为: ${SERVER_IP}"
    sed -i "s/^ServerIP=.*/ServerIP=${SERVER_IP}/g" "${INI_FILE}"
    sed -i "s/^localipAddress=.*/localipAddress=${SERVER_IP}/g" "${INI_FILE}"
    sed -i "s/^rtc.external-ip=.*/rtc.external-ip=${SERVER_IP}/g" "${INI_FILE}"
fi

# 如果配置了 HOOK_HOST，动态更新 Hook 回调主机地址
if [ -n "${HOOK_HOST}" ] && [ -f "${INI_FILE}" ]; then
    echo "🔧 动态更新 Hook 回调主机为: ${HOOK_HOST}"
    sed -i "s|http://host.docker.internal:[0-9]*|http://${HOOK_HOST}|g" "${INI_FILE}"
fi

# 如果配置了 HOOK_PORT，动态更新 Hook 回调端口
if [ -n "${HOOK_PORT}" ] && [ -f "${INI_FILE}" ]; then
    echo "🔧 动态更新 Hook 回调端口为: ${HOOK_PORT}"
    sed -i -E "s|(:[0-9]+)(/index/hook)|:${HOOK_PORT}\2|g" "${INI_FILE}"
fi

echo "=========================================================="
echo " 正在启动 ABLMediaServer..."
echo " 工作目录: ${WORK_DIR}"
echo " 配置文件: ${INI_FILE}"
echo "=========================================================="

# 启动主程序并处理退出信号
./ABLMediaServer &
PID=$!

trap "echo '收到终止信号，正在关闭 ABLMediaServer...'; kill -TERM $PID 2>/dev/null; wait $PID" SIGTERM SIGINT

wait $PID
