package org.dromara.network.core;


public interface Topics {

    /**
     * 服务向客户端发送执行命令消息的topic
     */
    String execute = "execute";

    /**
     * 客户端接收到execute命令向服务端返回结果时使用的topic
     */
    String reply = "reply";

    /**
     * 客户端直接上报数据时使用的topic
     */
    String report = "report";

    //单Topic上报
    String upAll = "/up/all/%s/%s";
    String downAll = "/down/all/%s/%s";

    //多Topic上报
    //主设备
    String up = "/up/%s/%s/%s";
    String down = "/down/%s/%s/%s";
    String upProperty = "/up/property/%s/%s";
    String downProperty = "/down/property/%s/%s";
    String upEvent = "/up/event/%s/%s";
    String downEvent = "/down/event/%s/%s";
    String upAction = "/up/action/%s/%s";
    String downAction = "/down/action/%s/%s";


    //子设备
    String upSubAll = "/up/sub//%s/%s";
    String downSubAll = "/down/sub//%s/%s";

    String upSub = "/up/sub/%s/%s/%s";
    String downSub = "/down/sub/%s/%s/%s";
    String upSubProperty = "/up/sub/property/%s/%s";
    String downSubProperty = "/down/sub/property/%s/%s";
    String upSubEvent = "/up/sub/event/%s/%s";
    String downSubEvent = "/down/sub/event/%s/%s";
    String upSubAction = "/up/sub/action/%s/%s";
    String downSubAction = "/down/sub/action/%s/%s";
}
