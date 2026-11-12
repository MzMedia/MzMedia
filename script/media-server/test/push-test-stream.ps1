# ==============================================================================
# FFmpeg 模拟摄像头推流测试脚本 (Windows PowerShell)
# ==============================================================================
param (
    [string]$Target = "zlm"
)

if ($Target -eq "abl") {
    $StreamUrl = "rtmp://127.0.0.1:1936/live/test"
    $PlayFlv   = "http://127.0.0.1:9001/live/test.flv"
    $PlayWs    = "ws://127.0.0.1:9002/live/test.flv"
    $PlayRtsp  = "rtsp://127.0.0.1:5544/live/test"
    $Name      = "ABLMediaServer"
} else {
    $StreamUrl = "rtmp://127.0.0.1:1935/live/test"
    $PlayFlv   = "http://127.0.0.1:18080/live/test.live.flv"
    $PlayWs    = "ws://127.0.0.1:18080/live/test.live.flv"
    $PlayRtsp  = "rtsp://127.0.0.1:554/live/test"
    $Name      = "ZLMediaKit"
}

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host " 正在向 $Name 推送测试视频流..." -ForegroundColor Cyan
Write-Host " 推流地址 : $StreamUrl" -ForegroundColor Yellow
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "播流地址测试参考:" -ForegroundColor White
Write-Host " - HTTP-FLV : $PlayFlv" -ForegroundColor Gray
Write-Host " - WS-FLV   : $PlayWs" -ForegroundColor Gray
Write-Host " - RTSP     : $PlayRtsp" -ForegroundColor Gray
Write-Host "==========================================================" -ForegroundColor Cyan

$ffmpegExists = (Get-Command ffmpeg -ErrorAction SilentlyContinue)

if ($ffmpegExists) {
    Write-Host "[推流中] 按 Ctrl + C 停止推流..." -ForegroundColor Green
    ffmpeg -re -f lavfi -i testsrc=size=1280x720:rate=25 `
           -f lavfi -i sine=frequency=1000:sample_rate=44100 `
           -vcodec libx264 -preset ultrafast -tune zerolatency -b:v 1500k `
           -acodec aac -b:a 128k `
           -f flv $StreamUrl
} else {
    Write-Host "[提示] 本机未安装本地 ffmpeg 命令，通过 Docker 临时运行推流..." -ForegroundColor Yellow
    docker run --rm -it --add-host host.docker.internal:host-gateway jrottenberg/ffmpeg:latest `
        -re -f lavfi -i testsrc=size=1280x720:rate=25 `
        -f lavfi -i sine=frequency=1000:sample_rate=44100 `
        -vcodec libx264 -preset ultrafast -tune zerolatency -b:v 1500k `
        -acodec aac -b:a 128k `
        -f flv $StreamUrl.Replace("127.0.0.1", "host.docker.internal")
}
