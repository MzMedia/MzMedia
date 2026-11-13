@echo off
rem ==============================================================================
rem ZLMediaKit 单容器快速启动脚本 (Windows)
rem ==============================================================================

setlocal enabledelayedexpansion
set "SCRIPT_DIR=%~dp0"
set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"

if "%ZLM_HTTP_PORT%"=="" set "ZLM_HTTP_PORT=18080"
if "%ZLM_RTSP_PORT%"=="" set "ZLM_RTSP_PORT=8554"
if "%ZLM_RTMP_PORT%"=="" set "ZLM_RTMP_PORT=19350"
if "%ZLM_RTC_PORT%"=="" set "ZLM_RTC_PORT=8005"
if "%ZLM_RTP_PORT%"=="" set "ZLM_RTP_PORT=10005"

echo ==========================================================
echo  正在启动 ZLMediaKit 流媒体服务器 (Docker 容器模式)...
echo  HTTP/API 端口 : %ZLM_HTTP_PORT%
echo  RTSP 端口     : %ZLM_RTSP_PORT%
echo  RTMP 端口     : %ZLM_RTMP_PORT%
echo  WebRTC 端口   : %ZLM_RTC_PORT%/udp
echo  RTP 端口      : %ZLM_RTP_PORT%
echo ==========================================================

rem 确保数据目录存在
if not exist "%SCRIPT_DIR%\data\record" mkdir "%SCRIPT_DIR%\data\record"
if not exist "%SCRIPT_DIR%\data\snap" mkdir "%SCRIPT_DIR%\data\snap"

rem 清理已有旧容器
docker rm -f mz-zlm >nul 2>&1

docker run -d ^
  --name mz-zlm ^
  --restart unless-stopped ^
  --add-host host.docker.internal:host-gateway ^
  -p %ZLM_HTTP_PORT%:80 ^
  -p 18443:443 ^
  -p %ZLM_RTSP_PORT%:554 ^
  -p %ZLM_RTMP_PORT%:1935 ^
  -p %ZLM_RTC_PORT%:8000/udp ^
  -p %ZLM_RTP_PORT%:10000 ^
  -p %ZLM_RTP_PORT%:10000/udp ^
  -p 32000-32050:32000-32050/udp ^
  -v "%SCRIPT_DIR%\config.ini:/opt/media/conf/config.ini:ro" ^
  -v "%SCRIPT_DIR%\data\record:/opt/media/bin/www/record" ^
  -v "%SCRIPT_DIR%\data\snap:/opt/media/bin/www/snap" ^
  zlmediakit/zlmediakit:master

echo.
echo ZLMediaKit 启动完成！
echo 检查状态: docker ps -f name=mz-zlm
echo API 验证: curl http://127.0.0.1:%ZLM_HTTP_PORT%/index/api/getServerConfig?secret=mzmedia_zlm_secret_2026_test_key
pause
