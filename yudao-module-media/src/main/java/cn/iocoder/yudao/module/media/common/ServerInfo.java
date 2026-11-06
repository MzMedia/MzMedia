package cn.iocoder.yudao.module.media.common;

import cn.iocoder.yudao.module.media.utils.DateUtil;
import lombok.Data;

@Data
public class ServerInfo {

    private String serverId;
    private String ip;
    private int port;
    private String createTime;

    public static ServerInfo create(String ip, int port) {
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setIp(ip);
        serverInfo.setPort(port);
        serverInfo.setCreateTime(DateUtil.getNow());
        return serverInfo;
    }
}
