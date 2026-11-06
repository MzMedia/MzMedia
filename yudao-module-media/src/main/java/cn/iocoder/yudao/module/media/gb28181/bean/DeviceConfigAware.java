package cn.iocoder.yudao.module.media.gb28181.bean;

import org.dom4j.Element;

public interface DeviceConfigAware {

    String configType();

    void fromXml(Element element);
}
