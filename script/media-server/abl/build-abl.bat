@echo off
rem ==============================================================================
rem ABLMediaServer Docker 镜像构建脚本 (Windows)
rem ==============================================================================
setlocal enabledelayedexpansion

set "SCRIPT_DIR=%~dp0"
set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"
cd /d "%SCRIPT_DIR%"

set "IMAGE_NAME=%~1"
if "%IMAGE_NAME%"=="" set "IMAGE_NAME=ablmediaserver:latest"
set "DOWNLOAD_URL=https://github.com/zhichao281/ABLMediaServer/releases/download/2025-10-14/Linux-webrtc.zip"

echo ==========================================================
echo  正在准备构建 ABLMediaServer 镜像: %IMAGE_NAME%
echo ==========================================================

if not exist "Linux-webrtc.zip" if not exist "ABLMediaServer" (
    echo [提示] 本地未检测到 Linux-webrtc.zip，尝试预下载以加速构建...
    echo 下载地址: %DOWNLOAD_URL%
    powershell -Command "try { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object Net.WebClient).DownloadFile('%DOWNLOAD_URL%', 'Linux-webrtc.zip'); Write-Host '[成功] 下载完成' } catch { Write-Host '[警告] 下载遇到异常，将交由 Dockerfile 自动构建下载: ' $_.Exception.Message }"
)

echo.
echo [Docker] 开始构建 Docker 镜像...
docker build -t %IMAGE_NAME% -t mzmedia/ablmediaserver:latest .

if %errorlevel% equ 0 (
    echo.
    echo ==========================================================
    echo [成功] ABLMediaServer 镜像构建完成: %IMAGE_NAME%
    echo 可以使用以下命令运行测试:
    echo   run-abl.bat
    echo 或在上一级目录使用 docker compose:
    echo   cd .. ^& docker compose up -d abl
    echo ==========================================================
) else (
    echo.
    echo [错误] 镜像构建失败，请检查 Docker 服务状态及网络连接。
)

pause
