# ==============================================================================
# ZLM / ABL 流媒体服务器 REST API 联通性自动化探测脚本 (PowerShell)
# ==============================================================================

$Secret = if ($env:MEDIA_SECRET) { $env:MEDIA_SECRET } else { "mzmedia_zlm_secret_2026_test_key" }
$ZlmPort = if ($env:ZLM_HTTP_PORT) { $env:ZLM_HTTP_PORT } else { 18080 }
$AblPort = if ($env:ABL_HTTP_PORT) { $env:ABL_HTTP_PORT } else { 9000 }

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host " 正在探测 ZLMediaKit 与 ABLMediaServer REST API 联通性" -ForegroundColor Cyan
Write-Host " 鉴权密钥 (Secret): $Secret" -ForegroundColor Gray
Write-Host "==========================================================" -ForegroundColor Cyan

Write-Host "`n----------------------------------------------------------" -ForegroundColor Yellow
Write-Host " 1. 探测 ZLMediaKit (ZLM) -> http://127.0.0.1:$ZlmPort" -ForegroundColor Yellow
Write-Host "----------------------------------------------------------" -ForegroundColor Yellow

try {
    $zlmUrl = "http://127.0.0.1:$ZlmPort/index/api/getServerConfig?secret=$Secret"
    $response = Invoke-RestMethod -Uri $zlmUrl -Method Get -TimeoutSec 4 -ErrorAction Stop
    Write-Host "[成功] ZLM 响应代码: $($response.code)" -ForegroundColor Green
    if ($response.data) {
        Write-Host "  - 服务ID   : $($response.data[0].'general.mediaServerId')" -ForegroundColor White
        Write-Host "  - HTTP端口 : $($response.data[0].'http.port')" -ForegroundColor White
        Write-Host "  - RTSP端口 : $($response.data[0].'rtsp.port')" -ForegroundColor White
        Write-Host "  - RTMP端口 : $($response.data[0].'rtmp.port')" -ForegroundColor White
        Write-Host "  - Hook开关 : $($response.data[0].'hook.enable')" -ForegroundColor White
    }
} catch {
    Write-Host "[失败] 无法连通 ZLMediaKit (端口 $ZlmPort): $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n----------------------------------------------------------" -ForegroundColor Yellow
Write-Host " 2. 探测 ABLMediaServer (ABL) -> http://127.0.0.1:$AblPort" -ForegroundColor Yellow
Write-Host "----------------------------------------------------------" -ForegroundColor Yellow

try {
    $ablUrl = "http://127.0.0.1:$AblPort/index/api/getServerConfig?secret=$Secret"
    $response = Invoke-RestMethod -Uri $ablUrl -Method Get -TimeoutSec 4 -ErrorAction Stop
    Write-Host "[成功] ABL 响应代码: $($response.code)" -ForegroundColor Green
    if ($response.params) {
        Write-Host "  - 核心参数已成功读取，返回项数: $($response.params.Count)" -ForegroundColor White
    }
} catch {
    Write-Host "[失败] 无法连通 ABLMediaServer (端口 $AblPort): $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n==========================================================" -ForegroundColor Cyan
Write-Host "探测完成！" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan
