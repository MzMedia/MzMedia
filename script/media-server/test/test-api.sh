#!/bin/bash
# ==============================================================================
# ZLM / ABL 流媒体服务器 REST API 联通性自动化探测脚本
# ==============================================================================

SECRET="${MEDIA_SECRET:-mzmedia_zlm_secret_2026_test_key}"
ZLM_PORT="${ZLM_HTTP_PORT:-18080}"
ABL_PORT="${ABL_HTTP_PORT:-9000}"

echo "=========================================================="
echo " 正在探测 ZLMediaKit 与 ABLMediaServer REST API 联通性"
echo " 鉴权密钥 (Secret): ${SECRET}"
echo "=========================================================="

echo ""
echo "----------------------------------------------------------"
echo "🔍 1. 探测 ZLMediaKit (ZLM) -> http://127.0.0.1:${ZLM_PORT}"
echo "----------------------------------------------------------"
ZLM_RES=$(curl -s -m 5 "http://127.0.0.1:${ZLM_PORT}/index/api/getServerConfig?secret=${SECRET}" || true)
if [ -n "${ZLM_RES}" ]; then
    echo "✅ ZLM API 响应正常:"
    echo "${ZLM_RES}" | head -c 300
    echo "...(略)"
    echo ""
    echo "📋 获取 ZLM 在线媒体流列表:"
    curl -s -m 5 "http://127.0.0.1:${ZLM_PORT}/index/api/getMediaList?secret=${SECRET}" || true
    echo ""
else
    echo "❌ 无法连接到 ZLMediaKit (端口 ${ZLM_PORT})，请确认容器是否已启动！"
fi

echo ""
echo "----------------------------------------------------------"
echo "🔍 2. 探测 ABLMediaServer (ABL) -> http://127.0.0.1:${ABL_PORT}"
echo "----------------------------------------------------------"
ABL_RES=$(curl -s -m 5 "http://127.0.0.1:${ABL_PORT}/index/api/getServerConfig?secret=${SECRET}" || true)
if [ -n "${ABL_RES}" ]; then
    echo "✅ ABL API 响应正常:"
    echo "${ABL_RES}" | head -c 300
    echo "...(略)"
    echo ""
    echo "📋 获取 ABL 在线媒体流列表:"
    curl -s -m 5 "http://127.0.0.1:${ABL_PORT}/index/api/getMediaList?secret=${SECRET}" || true
    echo ""
else
    echo "❌ 无法连接到 ABLMediaServer (端口 ${ABL_PORT})，请确认容器是否已启动！"
fi

echo ""
echo "=========================================================="
echo "探测完成！"
echo "=========================================================="
