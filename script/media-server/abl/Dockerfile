# ==============================================================================
# ABLMediaServer Dockerfile
# 支持自动在线下载官方 Release (Linux-webrtc.zip) 或使用本地离线包构建
# ==============================================================================
FROM ubuntu:22.04

LABEL maintainer="mz-media"
LABEL description="ABLMediaServer Docker image for mz-media GB28181/RTSP/RTMP streaming test"

ENV DEBIAN_FRONTEND=noninteractive \
    TZ=Asia/Shanghai \
    WORK_DIR=/opt/abl

# 默认官方下载地址，可通过 --build-arg DOWNLOAD_URL=... 覆盖
ARG DOWNLOAD_URL=https://github.com/zhichao281/ABLMediaServer/releases/download/2025-10-14/Linux-webrtc.zip

# 安装基础运行依赖与常用工具（OpenSSL、FFmpeg、tzdata 等）
RUN apt-get update && apt-get install -y --no-install-recommends \
    ca-certificates \
    curl \
    unzip \
    libssl3 \
    libgomp1 \
    ffmpeg \
    tzdata \
    procps \
    && ln -fs /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo ${TZ} > /etc/timezone \
    && rm -rf /var/lib/apt/lists/*

WORKDIR ${WORK_DIR}

# 复制本地 context 下的内容（如果本地已放置了 Linux-webrtc.zip 或解压后的文件）
COPY . /tmp/abl_context/

# 安装逻辑：优先检测本地离线包，若不存在则通过 curl 自动下载
RUN if [ -f /tmp/abl_context/Linux-webrtc.zip ]; then \
        echo "📦 检测到本地 Linux-webrtc.zip，正在解压..." && \
        unzip -q /tmp/abl_context/Linux-webrtc.zip -d /tmp/abl_unzip; \
    elif [ -f /tmp/abl_context/ABLMediaServer ]; then \
        echo "📦 检测到本地已解压的 ABL 文件..." && \
        mkdir -p /tmp/abl_unzip && cp -rf /tmp/abl_context/* /tmp/abl_unzip/; \
    else \
        echo "🌐 本地未提供压缩包，从官方 Release 下载: ${DOWNLOAD_URL}..." && \
        curl -fsSL --retry 3 --retry-delay 3 "${DOWNLOAD_URL}" -o /tmp/Linux-webrtc.zip && \
        unzip -q /tmp/Linux-webrtc.zip -d /tmp/abl_unzip && \
        rm -f /tmp/Linux-webrtc.zip; \
    fi && \
    # 将解压后的核心文件移入 /opt/abl
    ( cp -rf /tmp/abl_unzip/*/* ${WORK_DIR}/ 2>/dev/null || cp -rf /tmp/abl_unzip/* ${WORK_DIR}/ 2>/dev/null || true ) && \
    rm -rf /tmp/abl_context /tmp/abl_unzip

# 复制定制的配置文件和入口脚本
COPY ABLMediaServer.ini ${WORK_DIR}/ABLMediaServer.ini
COPY entrypoint.sh /entrypoint.sh

# 授权与初始化目录
RUN chmod +x /entrypoint.sh && \
    chmod +x ${WORK_DIR}/ABLMediaServer || true && \
    mkdir -p ${WORK_DIR}/record ${WORK_DIR}/picture

ENV LD_LIBRARY_PATH=${WORK_DIR}

# 开放 ABL 端口
EXPOSE 9000 9001 9002 9003 9004 5544 1936 7078 10002 9005/udp 31000-31050/udp

ENTRYPOINT ["/entrypoint.sh"]
