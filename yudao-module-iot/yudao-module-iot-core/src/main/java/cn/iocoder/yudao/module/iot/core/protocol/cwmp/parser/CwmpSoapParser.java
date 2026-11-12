package cn.iocoder.yudao.module.iot.core.protocol.cwmp.parser;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.iot.core.protocol.cwmp.model.*;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TR-069 CWMP SOAP XML 消息解析器
 *
 * 基于 dom4j 解析 CPE 发送的 SOAP 报文
 *
 * @author 芋道源码
 */
@Slf4j
public class CwmpSoapParser {

    /**
     * 获取 SOAP 报文的方法类型（如 Inform, GetParameterValuesResponse, Fault 等）
     *
     * @param xml SOAP XML 报文
     * @return 方法名，如 "Inform", "GetParameterValuesResponse", "Fault"
     */
    public static String getMessageType(String xml) {
        if (StrUtil.isBlank(xml)) {
            return "";
        }
        try {
            Document doc = DocumentHelper.parseText(xml);
            Element root = doc.getRootElement();
            Element body = findChildElementIgnoreNamespace(root, "Body");
            if (body == null) {
                return "";
            }
            Iterator<Element> it = body.elementIterator();
            if (it.hasNext()) {
                Element methodElem = it.next();
                return methodElem.getName();
            }
        } catch (Exception e) {
            log.warn("[getMessageType][XML 解析失败: {}]", e.getMessage());
        }
        return "";
    }

    /**
     * 获取 SOAP Header 中的 ID
     *
     * @param xml SOAP XML 报文
     * @return 消息 ID
     */
    public static String getHeaderId(String xml) {
        if (StrUtil.isBlank(xml)) {
            return null;
        }
        try {
            Document doc = DocumentHelper.parseText(xml);
            Element root = doc.getRootElement();
            Element header = findChildElementIgnoreNamespace(root, "Header");
            if (header != null) {
                Element idElem = findChildElementIgnoreNamespace(header, "ID");
                if (idElem != null) {
                    return idElem.getTextTrim();
                }
            }
        } catch (Exception e) {
            log.warn("[getHeaderId][解析 Header ID 失败: {}]", e.getMessage());
        }
        return null;
    }

    /**
     * 解析 Inform 报文
     *
     * @param xml SOAP XML
     * @return CwmpInform 对象
     */
    public static CwmpInform parseInform(String xml) {
        try {
            Document doc = DocumentHelper.parseText(xml);
            Element root = doc.getRootElement();
            Element body = findChildElementIgnoreNamespace(root, "Body");
            if (body == null) {
                return null;
            }
            Element informElem = findChildElementIgnoreNamespace(body, "Inform");
            if (informElem == null) {
                return null;
            }

            CwmpInform inform = new CwmpInform();
            inform.setHeaderId(getHeaderId(xml));

            // 1. DeviceId
            Element deviceIdElem = findChildElementIgnoreNamespace(informElem, "DeviceId");
            if (deviceIdElem != null) {
                CwmpDeviceId deviceId = CwmpDeviceId.builder()
                        .manufacturer(getElementText(deviceIdElem, "Manufacturer"))
                        .oui(getElementText(deviceIdElem, "OUI"))
                        .productClass(getElementText(deviceIdElem, "ProductClass"))
                        .serialNumber(getElementText(deviceIdElem, "SerialNumber"))
                        .build();
                inform.setDeviceId(deviceId);
            }

            // 2. Events
            Element eventElem = findChildElementIgnoreNamespace(informElem, "Event");
            if (eventElem != null) {
                List<CwmpEvent> events = new ArrayList<>();
                List<Element> eventStructs = findChildElementsIgnoreNamespace(eventElem, "EventStruct");
                for (Element es : eventStructs) {
                    events.add(CwmpEvent.builder()
                            .eventCode(getElementText(es, "EventCode"))
                            .commandKey(getElementText(es, "CommandKey"))
                            .build());
                }
                inform.setEvents(events);
            }

            // 3. CurrentTime & RetryCount
            inform.setCurrentTime(getElementText(informElem, "CurrentTime"));
            String retryCountStr = getElementText(informElem, "RetryCount");
            if (NumberUtil.isInteger(retryCountStr)) {
                inform.setRetryCount(Integer.parseInt(retryCountStr));
            }

            // 4. ParameterList
            Element paramListElem = findChildElementIgnoreNamespace(informElem, "ParameterList");
            if (paramListElem != null) {
                List<CwmpParameterValue> params = new ArrayList<>();
                List<Element> pvsList = findChildElementsIgnoreNamespace(paramListElem, "ParameterValueStruct");
                for (Element pvs : pvsList) {
                    Element nameElem = findChildElementIgnoreNamespace(pvs, "Name");
                    Element valElem = findChildElementIgnoreNamespace(pvs, "Value");
                    if (nameElem != null) {
                        String name = nameElem.getTextTrim();
                        String val = valElem != null ? valElem.getText() : "";
                        String type = valElem != null ? valElem.attributeValue("type") : "";
                        params.add(CwmpParameterValue.builder()
                                .name(name)
                                .value(val)
                                .type(type)
                                .build());
                    }
                }
                inform.setParameterList(params);
            }

            return inform;
        } catch (Exception e) {
            log.error("[parseInform][解析 Inform XML 异常]", e);
            return null;
        }
    }

    /**
     * 解析 GetParameterValuesResponse 报文
     *
     * @param xml SOAP XML
     * @return 参数键值对列表
     */
    public static List<CwmpParameterValue> parseGetParameterValuesResponse(String xml) {
        List<CwmpParameterValue> result = new ArrayList<>();
        try {
            Document doc = DocumentHelper.parseText(xml);
            Element root = doc.getRootElement();
            Element body = findChildElementIgnoreNamespace(root, "Body");
            if (body == null) {
                return result;
            }
            Element respElem = findChildElementIgnoreNamespace(body, "GetParameterValuesResponse");
            if (respElem == null) {
                return result;
            }
            Element paramListElem = findChildElementIgnoreNamespace(respElem, "ParameterList");
            if (paramListElem == null) {
                return result;
            }
            List<Element> pvsList = findChildElementsIgnoreNamespace(paramListElem, "ParameterValueStruct");
            for (Element pvs : pvsList) {
                Element nameElem = findChildElementIgnoreNamespace(pvs, "Name");
                Element valElem = findChildElementIgnoreNamespace(pvs, "Value");
                if (nameElem != null) {
                    String name = nameElem.getTextTrim();
                    String val = valElem != null ? valElem.getText() : "";
                    String type = valElem != null ? valElem.attributeValue("type") : "";
                    result.add(CwmpParameterValue.builder()
                            .name(name)
                            .value(val)
                            .type(type)
                            .build());
                }
            }
        } catch (Exception e) {
            log.error("[parseGetParameterValuesResponse][解析异常]", e);
        }
        return result;
    }

    /**
     * 解析 GetParameterNamesResponse 报文
     *
     * @param xml SOAP XML
     * @return 参数信息列表
     */
    public static List<CwmpParameterInfo> parseGetParameterNamesResponse(String xml) {
        List<CwmpParameterInfo> result = new ArrayList<>();
        try {
            Document doc = DocumentHelper.parseText(xml);
            Element root = doc.getRootElement();
            Element body = findChildElementIgnoreNamespace(root, "Body");
            if (body == null) {
                return result;
            }
            Element respElem = findChildElementIgnoreNamespace(body, "GetParameterNamesResponse");
            if (respElem == null) {
                return result;
            }
            Element paramListElem = findChildElementIgnoreNamespace(respElem, "ParameterList");
            if (paramListElem == null) {
                return result;
            }
            List<Element> infoList = findChildElementsIgnoreNamespace(paramListElem, "ParameterInfoStruct");
            for (Element elem : infoList) {
                Element nameElem = findChildElementIgnoreNamespace(elem, "Name");
                Element writableElem = findChildElementIgnoreNamespace(elem, "Writable");
                if (nameElem != null) {
                    String name = nameElem.getTextTrim();
                    boolean writable = false;
                    if (writableElem != null) {
                        String wStr = writableElem.getTextTrim();
                        writable = "1".equals(wStr) || "true".equalsIgnoreCase(wStr);
                    }
                    result.add(CwmpParameterInfo.builder()
                            .name(name)
                            .writable(writable)
                            .build());
                }
            }
        } catch (Exception e) {
            log.error("[parseGetParameterNamesResponse][解析异常]", e);
        }
        return result;
    }

    /**
     * 解析 SOAP Fault 报文
     *
     * @param xml SOAP XML
     * @return CwmpFault 对象
     */
    public static CwmpFault parseFault(String xml) {
        try {
            Document doc = DocumentHelper.parseText(xml);
            Element root = doc.getRootElement();
            Element body = findChildElementIgnoreNamespace(root, "Body");
            if (body == null) {
                return null;
            }
            Element faultElem = findChildElementIgnoreNamespace(body, "Fault");
            if (faultElem == null) {
                return null;
            }

            Element detailElem = findChildElementIgnoreNamespace(faultElem, "detail");
            Element cwmpFaultElem = detailElem != null ? findChildElementIgnoreNamespace(detailElem, "Fault") : null;
            Element targetFaultElem = cwmpFaultElem != null ? cwmpFaultElem : faultElem;

            String faultCodeStr = getElementText(targetFaultElem, "FaultCode");
            String faultString = getElementText(targetFaultElem, "FaultString");

            CwmpFault cwmpFault = CwmpFault.builder()
                    .faultCode(NumberUtil.isInteger(faultCodeStr) ? Integer.parseInt(faultCodeStr) : 9002)
                    .faultString(faultString)
                    .build();

            // 解析 SetParameterValuesFault
            List<Element> spvFaultElems = findChildElementsIgnoreNamespace(targetFaultElem, "SetParameterValuesFault");
            if (!spvFaultElems.isEmpty()) {
                List<CwmpFault.ParameterFault> paramFaults = new ArrayList<>();
                for (Element spvf : spvFaultElems) {
                    String pName = getElementText(spvf, "ParameterName");
                    String pCodeStr = getElementText(spvf, "FaultCode");
                    String pStr = getElementText(spvf, "FaultString");
                    paramFaults.add(new CwmpFault.ParameterFault(
                            pName,
                            NumberUtil.isInteger(pCodeStr) ? Integer.parseInt(pCodeStr) : 9003,
                            pStr));
                }
                cwmpFault.setSetParameterValuesFaults(paramFaults);
            }
            return cwmpFault;
        } catch (Exception e) {
            log.error("[parseFault][解析 Fault 异常]", e);
            return null;
        }
    }

    // ==================== 辅助 DOM 解析工具方法（忽略命名空间前缀） ====================

    private static Element findChildElementIgnoreNamespace(Element parent, String localName) {
        if (parent == null) {
            return null;
        }
        Iterator<Element> it = parent.elementIterator();
        while (it.hasNext()) {
            Element child = it.next();
            if (localName.equalsIgnoreCase(child.getName())) {
                return child;
            }
        }
        return null;
    }

    private static List<Element> findChildElementsIgnoreNamespace(Element parent, String localName) {
        List<Element> list = new ArrayList<>();
        if (parent == null) {
            return list;
        }
        Iterator<Element> it = parent.elementIterator();
        while (it.hasNext()) {
            Element child = it.next();
            if (localName.equalsIgnoreCase(child.getName())) {
                list.add(child);
            }
        }
        return list;
    }

    private static String getElementText(Element parent, String localName) {
        Element child = findChildElementIgnoreNamespace(parent, localName);
        return child != null ? child.getTextTrim() : null;
    }

}
