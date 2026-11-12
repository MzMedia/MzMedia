#!/bin/bash
# ==============================================================================
# MzMedia 流媒体服务测试启动管理脚本 (Linux / macOS)
# ==============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${SCRIPT_DIR}"

ACTION="${1:-help}"

function show_help() {
    echo "=========================================================="
    echo " MzMedia 流媒体服务器测试启动脚本 (ZLM & ABL)"
    echo "=========================================================="
    echo "用法: ./start.sh [命令]"
    echo ""
    echo "可用命令:"
    echo "  all       同时构建并启动 ZLM 与 ABL 流媒体服务器"
    echo "  zlm       仅启动 ZLMediaKit 流媒体服务器"
    echo "  abl       构建并启动 ABLMediaServer 流媒体服务器"
    echo "  stop      停止并移除当前测试流媒体容器"
    echo "  restart   重启所有测试流媒体服务"
    echo "  status    查看当前流媒体容器运行状态"
    echo "  logs      查看流媒体运行日志 (例如: ./start.sh logs zlm)"
    echo "  test      执行 RESTful API 联通性自动化探测"
    echo "  push      推流测试 (使用 FFmpeg 推送测试流)"
    echo "=========================================================="
}

case "${ACTION}" in
    all)
        echo "🚀 正在启动 ZLM 和 ABL 完整测试环境..."
        docker compose up -d --build
        echo ""
        echo "✅ 启动完成！容器列表:"
        docker compose ps
        ;;
    zlm)
        echo "🚀 正在启动 ZLMediaKit (ZLM)..."
        docker compose up -d zlm
        echo ""
        echo "✅ ZLM 启动完成！"
        docker compose ps zlm
        ;;
    abl)
        echo "🚀 正在启动 ABLMediaServer (ABL)..."
        docker compose up -d --build abl
        echo ""
        echo "✅ ABL 启动完成！"
        docker compose ps abl
        ;;
    stop)
        echo "🛑 正在停止测试流媒体服务..."
        docker compose down
        echo "✅ 已全部停止！"
        ;;
    restart)
        echo "🔄 正在重启测试流媒体服务..."
        docker compose restart
        docker compose ps
        ;;
    status)
        docker compose ps
        ;;
    logs)
        TARGET="${2:-}"
        docker compose logs -f --tail=100 ${TARGET}
        ;;
    test)
        if [ -f "./test/test-api.sh" ]; then
            bash ./test/test-api.sh
        else
            echo "❌ 未找到 ./test/test-api.sh"
        fi
        ;;
    push)
        TARGET="${2:-zlm}"
        if [ -f "./test/push-test-stream.sh" ]; then
            bash ./test/push-test-stream.sh "${TARGET}"
        else
            echo "❌ 未找到 ./test/push-test-stream.sh"
        fi
        ;;
    help|--help|-h|*)
        show_help
        ;;
esac
