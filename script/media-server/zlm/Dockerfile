FROM zlmediakit/zlmediakit:master

LABEL maintainer="mz-media"
LABEL description="ZLMediaKit custom test image for mz-media"

# 复制定制配置至镜像中
COPY config.ini /opt/media/conf/config.ini

# 创建必要的录像与截图目录
RUN mkdir -p /opt/media/bin/www/record /opt/media/bin/www/snap /opt/media/bin/ffmpeg

WORKDIR /opt/media/bin/

EXPOSE 80 443 554 1935 8000/udp 10000 10000/udp 30000-30050/udp

CMD ["./MediaServer", "-s", "default.pem", "-c", "../conf/config.ini", "-l", "0"]
