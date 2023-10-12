FROM zlmediakit/zlmediakit:master

EXPOSE 18081/tcp
EXPOSE 80/tcp
EXPOSE 1935/tcp
EXPOSE 554/tcp
EXPOSE 554/udp
EXPOSE 30000-30050/tcp
EXPOSE 30000-30050/udp

ENV LC_ALL zh_CN.UTF-8

RUN export DEBIAN_FRONTEND=noninteractive &&\
        apt-get update && \
        apt-get install -y --no-install-recommends openjdk-11-jre ca-certificates ffmpeg language-pack-zh-hans && \
        apt-get autoremove -y && \
        apt-get clean -y && \
        rm -rf /var/lib/apt/lists/*dic && \
        mkdir -p /opt/assist  /opt/assist/record/ \

RUN cd /opt/media/bin && \
        echo '#!/bin/bash' > run.sh && \
        echo 'java -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/opt/assist/heapdump/ -jar /fastbee-record.jar &' >> run.sh && \
        echo '/opt/media/bin/MediaServer -d -m 3 -s default.pem -c ../conf/config.ini' >> run.sh && \
        chmod +x run.sh

WORKDIR /opt/media/bin
CMD ["sh", "run.sh"]

