@echo off
rem ==============================================================================
rem ABLMediaServer 单容器快速启动脚本 (Windows)
rem ==============================================================================
setlocal enabledelayedexpansion

set "SCRIPT_DIR=%~dp0"
set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"

if "%ABL_HTTP_PORT%"=="" set "ABL_HTTP_PORT=9000"
if "%ABL_FLV_PORT%"=="" set "ABL_FLV_PORT=9001"
if "%ABL_WS_PORT%"=="" set "ABL_WS_PORT=9002"
if "%ABL_RTSP_PORT%"=="" set "ABL_RTSP_PORT=5544"
if "%ABL_RTMP_PORT%"=="" set "ABL_RTMP_PORT=1936"
if "%ABL_RTC_PORT%"=="" set "ABL_RTC_PORT=9005"
if "%ABL_RTP_PORT%"=="" set "ABL_RTP_PORT=10002"
if "%ABL_JT_PORT%"=="" set "ABL_JT_PORT=7078"

echo ==========================================================
echo  正在启动 ABLMediaServer 流媒体服务器 (Docker 容器模式)...
echo  HTTP API 端口 : %ABL_HTTP_PORT%
echo  HTTP-FLV 端口 : %ABL_FLV_PORT%
echo  WS-FLV 端口   : %ABL_WS_PORT%
echo  RTSP 端口     : %ABL_RTSP_PORT%
echo  RTMP 端口     : %ABL_RTMP_PORT%
echo  WebRTC 端口   : %ABL_RTC_PORT%/udp
echo  RTP 收流端口  : %ABL_RTP_PORT%
echo  JT1078 端口   : %ABL_JT_PORT%
echo ==========================================================

rem 确保数据目录存在
if not exist "%SCRIPT_DIR%\data\record" mkdir "%SCRIPT_DIR%\data\record"
if not exist "%SCRIPT_DIR%\data\picture" mkdir "%SCRIPT_DIR%\data\picture"

rem 检查本地镜像是否存在，不存在则先自动构建
docker image inspect ablmediaserver:latest >nul 2>&1
if %errorlevel% neq 0 (
    echo [提示] 未发现本地 ABLMediaServer 镜像，开始自动构建...
    call "%SCRIPT_DIR%\build-abl.bat"
)

rem 清理已有旧容器
docker rm -f mz-abl >nul 2>&1

docker run -d ^
  --name mz-abl ^
  --restart unless-stopped ^
  --add-host host.docker.internal:host-gateway ^
  -p %ABL_HTTP_PORT%:9000 ^
  -p %ABL_FLV_PORT%:9001 ^
  -p %ABL_WS_PORT%:9002 ^
  -p 9003:9003 ^
  -p 9004:9004 ^
  -p %ABL_RTSP_PORT%:5544 ^
  -p %ABL_RTMP_PORT%:1936 ^
  -p %ABL_RTP_PORT%:10002 ^
  -p %ABL_JT_PORT%:7078 ^
  -p %ABL_RTC_PORT%:9005/udp ^
  -p 31000-31050:31000-31050/udp ^
  -v "%SCRIPT_DIR%\ABLMediaServer.ini:/opt/abl/ABLMediaServer.ini" ^
  -v "%SCRIPT_DIR%\data\record:/opt/abl/record" ^
  -v "%SCRIPT_DIR%\data\picture:/opt/abl/picture" ^
  ablmediaserver:latest

echo.
echo ABLMediaServer 启动完成！
echo 检查状态: docker ps -f name=mz-abl
echo API 验证: curl http://127.0.0.1:%ABL_HTTP_PORT%/index/api/getServerConfig?secret=035c73f7-bb6b-4889-a715-d9eb2d1925cc
pause
