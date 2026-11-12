@echo off
rem ==============================================================================
rem MzMedia 流媒体服务测试启动管理脚本 (Windows)
rem ==============================================================================
setlocal enabledelayedexpansion

set "SCRIPT_DIR=%~dp0"
set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"
cd /d "%SCRIPT_DIR%"

set "ACTION=%~1"
if "%ACTION%"=="" goto MENU

if /i "%ACTION%"=="all" goto START_ALL
if /i "%ACTION%"=="zlm" goto START_ZLM
if /i "%ACTION%"=="abl" goto START_ABL
if /i "%ACTION%"=="stop" goto STOP_ALL
if /i "%ACTION%"=="restart" goto RESTART_ALL
if /i "%ACTION%"=="status" goto STATUS_ALL
if /i "%ACTION%"=="test" goto RUN_TEST
if /i "%ACTION%"=="push" goto RUN_PUSH
if /i "%ACTION%"=="help" goto SHOW_HELP

:MENU
cls
echo ==========================================================
echo  MzMedia 流媒体服务器测试管理面板 (ZLM & ABL)
echo ==========================================================
echo  1. 启动所有流媒体服务 (ZLM + ABL)
echo  2. 仅启动 ZLMediaKit (ZLM)
echo  3. 仅启动 ABLMediaServer (ABL)
echo  4. 停止所有测试流媒体容器
echo  5. 重启所有测试流媒体容器
echo  6. 查看容器运行状态
echo  7. 执行 API 联通性自动化探测
echo  8. 向流媒体服务器推送测试视频流 (FFmpeg)
echo  0. 退出
echo ==========================================================
set /p CHOICE="请输入操作编号 [1-8, 0]: "

if "%CHOICE%"=="1" goto START_ALL
if "%CHOICE%"=="2" goto START_ZLM
if "%CHOICE%"=="3" goto START_ABL
if "%CHOICE%"=="4" goto STOP_ALL
if "%CHOICE%"=="5" goto RESTART_ALL
if "%CHOICE%"=="6" goto STATUS_ALL
if "%CHOICE%"=="7" goto RUN_TEST
if "%CHOICE%"=="8" goto RUN_PUSH
if "%CHOICE%"=="0" exit /b 0
echo 无效选择，请重试。
pause
goto MENU

:START_ALL
echo [Docker] 正在构建并启动 ZLM 和 ABL 测试服务...
docker compose up -d --build
echo.
docker compose ps
pause
goto MENU

:START_ZLM
echo [Docker] 正在启动 ZLMediaKit...
docker compose up -d zlm
echo.
docker compose ps zlm
pause
goto MENU

:START_ABL
echo [Docker] 正在构建并启动 ABLMediaServer...
docker compose up -d --build abl
echo.
docker compose ps abl
pause
goto MENU

:STOP_ALL
echo [Docker] 正在停止流媒体容器...
docker compose down
echo [成功] 容器已停止。
pause
goto MENU

:RESTART_ALL
echo [Docker] 正在重启流媒体容器...
docker compose restart
echo.
docker compose ps
pause
goto MENU

:STATUS_ALL
docker compose ps
pause
goto MENU

:RUN_TEST
powershell -ExecutionPolicy Bypass -File "%SCRIPT_DIR%\test\test-api.ps1"
pause
goto MENU

:RUN_PUSH
powershell -ExecutionPolicy Bypass -File "%SCRIPT_DIR%\test\push-test-stream.ps1"
pause
goto MENU

:SHOW_HELP
echo ==========================================================
echo 用法: start.bat [all | zlm | abl | stop | restart | status | test | push]
echo ==========================================================
exit /b 0
