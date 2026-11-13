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

# 默认下载镜像加速地址（可通过 --build-arg DOWNLOAD_URL=... 覆盖）
ARG DOWNLOAD_URL=https://gh-proxy.com/https://github.com/zhichao281/ABLMediaServer/releases/download/2025-10-14/Linux-webrtc.zip

# 换用清华镜像源加速国内访问，并以非交互模式安装基础依赖（unzip、tzdata、ca-certificates等）
# ABLMediaServer 自带全部自研与转码动态库（libavcodec、libboost、libssl、libx264 等），无须额外安装大型多媒体库
RUN ( sed -i 's@//.*archive.ubuntu.com@//mirrors.tuna.tsinghua.edu.cn@g' /etc/apt/sources.list 2>/dev/null || true ) && \
    ( sed -i 's@//.*security.ubuntu.com@//mirrors.tuna.tsinghua.edu.cn@g' /etc/apt/sources.list 2>/dev/null || true ) && \
    apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
        ca-certificates \
        tzdata \
        unzip \
        curl \
        procps && \
    ln -fs /usr/share/zoneinfo/${TZ} /etc/localtime && \
    echo ${TZ} > /etc/timezone && \
    rm -rf /var/lib/apt/lists/*

WORKDIR ${WORK_DIR}

# 复制本地 build context 下的内容
COPY . /tmp/abl_context/

# 安装逻辑：优先检测本地离线包，若不存在则通过镜像源自动下载
RUN if [ -f /tmp/abl_context/Linux-webrtc.zip ]; then \
        echo "📦 检测到本地 Linux-webrtc.zip，正在解压..." && \
        unzip -q /tmp/abl_context/Linux-webrtc.zip -d /tmp/abl_unzip; \
    elif [ -f /tmp/abl_context/ABLMediaServer ]; then \
        echo "📦 检测到本地已解压的 ABL 文件..." && \
        mkdir -p /tmp/abl_unzip && cp -rf /tmp/abl_context/* /tmp/abl_unzip/; \
    else \
        echo "🌐 本地未提供压缩包，从 Release 下载: ${DOWNLOAD_URL}..." && \
        curl -fsSL --retry 3 --retry-delay 3 "${DOWNLOAD_URL}" -o /tmp/Linux-webrtc.zip && \
        unzip -q /tmp/Linux-webrtc.zip -d /tmp/abl_unzip && \
        rm -f /tmp/Linux-webrtc.zip; \
    fi && \
    # 将解压后的核心文件移入 /opt/abl（兼容扁平压缩包与多层嵌套目录）
    if [ -f /tmp/abl_unzip/ABLMediaServer ]; then \
        echo "✅ 在解压根目录发现 ABLMediaServer" && \
        cp -rf /tmp/abl_unzip/* ${WORK_DIR}/; \
    else \
        TARGET_DIR=$(find /tmp/abl_unzip -name ABLMediaServer -type f -exec dirname {} \; | head -n 1); \
        if [ -n "$TARGET_DIR" ]; then \
            echo "✅ 在子目录发现 ABLMediaServer: $TARGET_DIR" && \
            cp -rf "$TARGET_DIR"/* ${WORK_DIR}/; \
        else \
            echo "❌ 未在解压目录中找到 ABLMediaServer 文件" && exit 1; \
        fi; \
    fi && \
    rm -rf /tmp/abl_context /tmp/abl_unzip

# 复制定制配置文件与入口脚本
COPY ABLMediaServer.ini ${WORK_DIR}/ABLMediaServer.ini
COPY entrypoint.sh /entrypoint.sh

# 授权与初始化目录结构
RUN chmod +x /entrypoint.sh && \
    chmod +x ${WORK_DIR}/ABLMediaServer 2>/dev/null || true && \
    mkdir -p ${WORK_DIR}/record ${WORK_DIR}/picture /log/ABLMediaServer

ENV LD_LIBRARY_PATH=${WORK_DIR}

# 开放 ABL 常用端口
EXPOSE 9000 9001 9002 9003 9004 5544 1936 7078 10002 9005/udp 31000-31050/udp

ENTRYPOINT ["/entrypoint.sh"]
