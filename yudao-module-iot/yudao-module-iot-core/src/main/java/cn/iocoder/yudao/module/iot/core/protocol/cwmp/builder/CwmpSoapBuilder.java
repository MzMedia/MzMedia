package cn.iocoder.yudao.module.iot.core.protocol.cwmp.builder;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Collection;
import java.util.Map;

/**
 * TR-069 CWMP SOAP XML 消息构建器
 *
 * 生成符合 Broadband Forum TR-069 标准的 SOAP 报文
 *
 * @author 芋道源码
 */
public class CwmpSoapBuilder {

    private static final String SOAP_ENV_START =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<soap:Envelope xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" " +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
            "xmlns:cwmp=\"urn:dslforum-org:cwmp-1-0\" " +
            "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n";

    private static final String SOAP_ENV_END = "</soap:Envelope>";

    /**
     * 构建 InformResponse 响应报文
     *
     * @param headerId 对应的 Inform 请求头中的消息 ID
     * @return SOAP XML
     */
    public static String buildInformResponse(String headerId) {
        StringBuilder sb = new StringBuilder(SOAP_ENV_START);
        sb.append("  <soap:Header>\n");
        if (StrUtil.isNotBlank(headerId)) {
            sb.append("    <cwmp:ID soap:mustUnderstand=\"1\">").append(escapeXml(headerId)).append("</cwmp:ID>\n");
        }
        sb.append("  </soap:Header>\n");
        sb.append("  <soap:Body soap:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n");
        sb.append("    <cwmp:InformResponse>\n");
        sb.append("      <MaxEnvelopes>1</MaxEnvelopes>\n");
        sb.append("    </cwmp:InformResponse>\n");
        sb.append("  </soap:Body>\n");
        sb.append(SOAP_ENV_END);
        return sb.toString();
    }

    /**
     * 构建 GetParameterValues 报文
     *
     * @param parameterNames 需要读取的参数路径集合
     * @return SOAP XML
     */
    public static String buildGetParameterValues(Collection<String> parameterNames) {
        String msgId = IdUtil.simpleUUID();
        StringBuilder sb = new StringBuilder(SOAP_ENV_START);
        sb.append("  <soap:Header>\n");
        sb.append("    <cwmp:ID soap:mustUnderstand=\"1\">").append(msgId).append("</cwmp:ID>\n");
        sb.append("  </soap:Header>\n");
        sb.append("  <soap:Body soap:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n");
        sb.append("    <cwmp:GetParameterValues>\n");
        sb.append("      <ParameterNames soapenc:arrayType=\"xsd:string[").append(parameterNames.size()).append("]\">\n");
        for (String param : parameterNames) {
            sb.append("        <string>").append(escapeXml(param)).append("</string>\n");
        }
        sb.append("      </ParameterNames>\n");
        sb.append("    </cwmp:GetParameterValues>\n");
        sb.append("  </soap:Body>\n");
        sb.append(SOAP_ENV_END);
        return sb.toString();
    }

    /**
     * 构建 SetParameterValues 报文
     *
     * @param values 要设置的参数键值对
     * @return SOAP XML
     */
    public static String buildSetParameterValues(Map<String, String> values) {
        String msgId = IdUtil.simpleUUID();
        StringBuilder sb = new StringBuilder(SOAP_ENV_START);
        sb.append("  <soap:Header>\n");
        sb.append("    <cwmp:ID soap:mustUnderstand=\"1\">").append(msgId).append("</cwmp:ID>\n");
        sb.append("  </soap:Header>\n");
        sb.append("  <soap:Body soap:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n");
        sb.append("    <cwmp:SetParameterValues>\n");
        sb.append("      <ParameterList soapenc:arrayType=\"cwmp:ParameterValueStruct[").append(values.size()).append("]\">\n");
        for (Map.Entry<String, String> entry : values.entrySet()) {
            sb.append("        <ParameterValueStruct>\n");
            sb.append("          <Name>").append(escapeXml(entry.getKey())).append("</Name>\n");
            sb.append("          <Value xsi:type=\"xsd:string\">").append(escapeXml(entry.getValue())).append("</Value>\n");
            sb.append("        </ParameterValueStruct>\n");
        }
        sb.append("      </ParameterList>\n");
        sb.append("      <ParameterKey>").append(msgId.substring(0, 8)).append("</ParameterKey>\n");
        sb.append("    </cwmp:SetParameterValues>\n");
        sb.append("  </soap:Body>\n");
        sb.append(SOAP_ENV_END);
        return sb.toString();
    }

    /**
     * 构建 GetParameterNames 报文
     *
     * @param path 参数路径（如 "InternetGatewayDevice." 或 "Device.WiFi."）
     * @param nextLevel 是否只返回下一层级 (false=递归返回所有下级)
     * @return SOAP XML
     */
    public static String buildGetParameterNames(String path, boolean nextLevel) {
        String msgId = IdUtil.simpleUUID();
        StringBuilder sb = new StringBuilder(SOAP_ENV_START);
        sb.append("  <soap:Header>\n");
        sb.append("    <cwmp:ID soap:mustUnderstand=\"1\">").append(msgId).append("</cwmp:ID>\n");
        sb.append("  </soap:Header>\n");
        sb.append("  <soap:Body soap:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n");
        sb.append("    <cwmp:GetParameterNames>\n");
        sb.append("      <ParameterPath>").append(escapeXml(path)).append("</ParameterPath>\n");
        sb.append("      <NextLevel>").append(nextLevel ? "1" : "0").append("</NextLevel>\n");
        sb.append("    </cwmp:GetParameterNames>\n");
        sb.append("  </soap:Body>\n");
        sb.append(SOAP_ENV_END);
        return sb.toString();
    }

    /**
     * 构建 Reboot 远程重启报文
     *
     * @param commandKey 命令标识 Key
     * @return SOAP XML
     */
    public static String buildReboot(String commandKey) {
        String msgId = IdUtil.simpleUUID();
        StringBuilder sb = new StringBuilder(SOAP_ENV_START);
        sb.append("  <soap:Header>\n");
        sb.append("    <cwmp:ID soap:mustUnderstand=\"1\">").append(msgId).append("</cwmp:ID>\n");
        sb.append("  </soap:Header>\n");
        sb.append("  <soap:Body soap:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n");
        sb.append("    <cwmp:Reboot>\n");
        sb.append("      <CommandKey>").append(escapeXml(StrUtil.blankToDefault(commandKey, "REBOOT_" + msgId.substring(0, 8)))).append("</CommandKey>\n");
        sb.append("    </cwmp:Reboot>\n");
        sb.append("  </soap:Body>\n");
        sb.append(SOAP_ENV_END);
        return sb.toString();
    }

    /**
     * 构建 FactoryReset 恢复出厂设置报文
     *
     * @return SOAP XML
     */
    public static String buildFactoryReset() {
        String msgId = IdUtil.simpleUUID();
        return SOAP_ENV_START +
                "  <soap:Header>\n" +
                "    <cwmp:ID soap:mustUnderstand=\"1\">" + msgId + "</cwmp:ID>\n" +
                "  </soap:Header>\n" +
                "  <soap:Body soap:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
                "    <cwmp:FactoryReset/>\n" +
                "  </soap:Body>\n" +
                SOAP_ENV_END;
    }

    /**
     * 构建 Download 固件下载/升级报文
     *
     * @param fileType 文件类型，"1 Firmware Upgrade Image" 或 "3 Vendor Configuration File"
     * @param url 下载 URL
     * @param username 下载认证用户名（可为空）
     * @param password 下载认证密码（可为空）
     * @param fileSize 文件大小（字节，可为 0）
     * @param targetFileName 目标文件名（可为空）
     * @return SOAP XML
     */
    public static String buildDownload(String fileType, String url, String username, String password,
                                       long fileSize, String targetFileName) {
        String msgId = IdUtil.simpleUUID();
        StringBuilder sb = new StringBuilder(SOAP_ENV_START);
        sb.append("  <soap:Header>\n");
        sb.append("    <cwmp:ID soap:mustUnderstand=\"1\">").append(msgId).append("</cwmp:ID>\n");
        sb.append("  </soap:Header>\n");
        sb.append("  <soap:Body soap:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n");
        sb.append("    <cwmp:Download>\n");
        sb.append("      <CommandKey>").append("DOWNLOAD_").append(msgId.substring(0, 8)).append("</CommandKey>\n");
        sb.append("      <FileType>").append(escapeXml(StrUtil.blankToDefault(fileType, "1 Firmware Upgrade Image"))).append("</FileType>\n");
        sb.append("      <URL>").append(escapeXml(url)).append("</URL>\n");
        sb.append("      <Username>").append(escapeXml(StrUtil.nullToEmpty(username))).append("</Username>\n");
        sb.append("      <Password>").append(escapeXml(StrUtil.nullToEmpty(password))).append("</Password>\n");
        sb.append("      <FileSize>").append(fileSize).append("</FileSize>\n");
        sb.append("      <TargetFileName>").append(escapeXml(StrUtil.nullToEmpty(targetFileName))).append("</TargetFileName>\n");
        sb.append("      <DelaySeconds>0</DelaySeconds>\n");
        sb.append("      <SuccessURL></SuccessURL>\n");
        sb.append("      <FailureURL></FailureURL>\n");
        sb.append("    </cwmp:Download>\n");
        sb.append("  </soap:Body>\n");
        sb.append(SOAP_ENV_END);
        return sb.toString();
    }

    /**
     * 构建 AddObject 报文
     *
     * @param objectName 对象名（以点结尾，如 InternetGatewayDevice.LANDevice.1.WLANConfiguration.）
     * @param parameterKey 参数 Key
     * @return SOAP XML
     */
    public static String buildAddObject(String objectName, String parameterKey) {
        String msgId = IdUtil.simpleUUID();
        StringBuilder sb = new StringBuilder(SOAP_ENV_START);
        sb.append("  <soap:Header>\n");
        sb.append("    <cwmp:ID soap:mustUnderstand=\"1\">").append(msgId).append("</cwmp:ID>\n");
        sb.append("  </soap:Header>\n");
        sb.append("  <soap:Body soap:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n");
        sb.append("    <cwmp:AddObject>\n");
        sb.append("      <ObjectName>").append(escapeXml(objectName)).append("</ObjectName>\n");
        sb.append("      <ParameterKey>").append(escapeXml(StrUtil.blankToDefault(parameterKey, msgId.substring(0, 8)))).append("</ParameterKey>\n");
        sb.append("    </cwmp:AddObject>\n");
        sb.append("  </soap:Body>\n");
        sb.append(SOAP_ENV_END);
        return sb.toString();
    }

    /**
     * 构建 DeleteObject 报文
     *
     * @param objectName 对象实例名（以点结尾，如 InternetGatewayDevice.LANDevice.1.WLANConfiguration.2.）
     * @param parameterKey 参数 Key
     * @return SOAP XML
     */
    public static String buildDeleteObject(String objectName, String parameterKey) {
        String msgId = IdUtil.simpleUUID();
        StringBuilder sb = new StringBuilder(SOAP_ENV_START);
        sb.append("  <soap:Header>\n");
        sb.append("    <cwmp:ID soap:mustUnderstand=\"1\">").append(msgId).append("</cwmp:ID>\n");
        sb.append("  </soap:Header>\n");
        sb.append("  <soap:Body soap:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n");
        sb.append("    <cwmp:DeleteObject>\n");
        sb.append("      <ObjectName>").append(escapeXml(objectName)).append("</ObjectName>\n");
        sb.append("      <ParameterKey>").append(escapeXml(StrUtil.blankToDefault(parameterKey, msgId.substring(0, 8)))).append("</ParameterKey>\n");
        sb.append("    </cwmp:DeleteObject>\n");
        sb.append("  </soap:Body>\n");
        sb.append(SOAP_ENV_END);
        return sb.toString();
    }

    /**
     * XML 特殊字符转义
     */
    private static String escapeXml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&apos;");
    }

}
