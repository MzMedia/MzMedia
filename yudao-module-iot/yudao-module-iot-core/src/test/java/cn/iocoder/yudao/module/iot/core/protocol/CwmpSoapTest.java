package cn.iocoder.yudao.module.iot.core.protocol;

import cn.iocoder.yudao.module.iot.core.protocol.cwmp.builder.CwmpSoapBuilder;
import cn.iocoder.yudao.module.iot.core.protocol.cwmp.model.*;
import cn.iocoder.yudao.module.iot.core.protocol.cwmp.parser.CwmpSoapParser;
import cn.iocoder.yudao.module.iot.core.protocol.mapper.Tr181ThingModelMapper;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TR-069 CWMP SOAP XML 构建与解析测试
 */
public class CwmpSoapTest {

    @Test
    public void testParseInform() {
        String informXml =
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                "xmlns:soap-enc=\"http://schemas.xmlsoap.org/soap/encoding/\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                "xmlns:cwmp=\"urn:dslforum-org:cwmp-1-0\">\n" +
                "  <soap:Header>\n" +
                "    <cwmp:ID soap:mustUnderstand=\"1\">5058</cwmp:ID>\n" +
                "  </soap:Header>\n" +
                "  <soap:Body>\n" +
                "    <cwmp:Inform>\n" +
                "      <DeviceId>\n" +
                "        <Manufacturer>ADB Broadband</Manufacturer>\n" +
                "        <OUI>0013C8</OUI>\n" +
                "        <ProductClass>VV5522</ProductClass>\n" +
                "        <SerialNumber>SN123456789</SerialNumber>\n" +
                "      </DeviceId>\n" +
                "      <Event soap-enc:arrayType=\"cwmp:EventStruct[2]\">\n" +
                "        <EventStruct>\n" +
                "          <EventCode>0 BOOTSTRAP</EventCode>\n" +
                "          <CommandKey></CommandKey>\n" +
                "        </EventStruct>\n" +
                "        <EventStruct>\n" +
                "          <EventCode>1 BOOT</EventCode>\n" +
                "          <CommandKey></CommandKey>\n" +
                "        </EventStruct>\n" +
                "      </Event>\n" +
                "      <CurrentTime>2026-11-12T00:00:00Z</CurrentTime>\n" +
                "      <RetryCount>0</RetryCount>\n" +
                "      <ParameterList soap-enc:arrayType=\"cwmp:ParameterValueStruct[5]\">\n" +
                "        <ParameterValueStruct>\n" +
                "          <Name>InternetGatewayDevice.DeviceInfo.Manufacturer</Name>\n" +
                "          <Value xsi:type=\"xsd:string\">ADB Broadband</Value>\n" +
                "        </ParameterValueStruct>\n" +
                "        <ParameterValueStruct>\n" +
                "          <Name>InternetGatewayDevice.ManagementServer.ConnectionRequestURL</Name>\n" +
                "          <Value xsi:type=\"xsd:string\">http://192.168.1.1:7547/ConnectionRequest-123</Value>\n" +
                "        </ParameterValueStruct>\n" +
                "        <ParameterValueStruct>\n" +
                "          <Name>InternetGatewayDevice.DeviceInfo.SoftwareVersion</Name>\n" +
                "          <Value xsi:type=\"xsd:string\">V1.0.0-PROD</Value>\n" +
                "        </ParameterValueStruct>\n" +
                "        <ParameterValueStruct>\n" +
                "          <Name>InternetGatewayDevice.DeviceInfo.HardwareVersion</Name>\n" +
                "          <Value xsi:type=\"xsd:string\">HW-V2</Value>\n" +
                "        </ParameterValueStruct>\n" +
                "        <ParameterValueStruct>\n" +
                "          <Name>InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1.WANIPConnection.1.ExternalIPAddress</Name>\n" +
                "          <Value xsi:type=\"xsd:string\">112.96.12.34</Value>\n" +
                "        </ParameterValueStruct>\n" +
                "      </ParameterList>\n" +
                "    </cwmp:Inform>\n" +
                "  </soap:Body>\n" +
                "</soap:Envelope>";

        String msgType = CwmpSoapParser.getMessageType(informXml);
        assertEquals("Inform", msgType);

        String headerId = CwmpSoapParser.getHeaderId(informXml);
        assertEquals("5058", headerId);

        CwmpInform inform = CwmpSoapParser.parseInform(informXml);
        assertNotNull(inform);
        assertEquals("SN123456789", inform.getSerialNumber());
        assertEquals("ADB Broadband", inform.getManufacturer());
        assertEquals("0013C8", inform.getDeviceId().getOui());
        assertEquals("VV5522", inform.getProductClass());
        assertEquals("V1.0.0-PROD", inform.getSoftwareVersion());
        assertEquals("HW-V2", inform.getHardwareVersion());
        assertEquals("http://192.168.1.1:7547/ConnectionRequest-123", inform.getConnectionRequestUrl());
        assertEquals("112.96.12.34", inform.getExternalIpAddress());
        assertEquals("TR098", inform.getDataModelType());
        assertTrue(inform.hasBootstrapEvent());
        assertTrue(inform.hasBootEvent());
        assertFalse(inform.hasPeriodicEvent());

        // 测试物模型映射
        Map<String, Object> props = Tr181ThingModelMapper.mapCwmpParamsToThingModel(inform.getParameterList());
        assertEquals("ADB Broadband", props.get("manufacturer"));
        assertEquals("V1.0.0-PROD", props.get("firmwareVersion"));
        assertEquals("112.96.12.34", props.get("wanIp"));
    }

    @Test
    public void testBuildSoapXml() {
        // 1. InformResponse
        String informResp = CwmpSoapBuilder.buildInformResponse("5058");
        assertTrue(informResp.contains("<cwmp:InformResponse>"));
        assertTrue(informResp.contains("5058"));

        // 2. GetParameterValues
        String getPv = CwmpSoapBuilder.buildGetParameterValues(Arrays.asList("Device.DeviceInfo.", "Device.WiFi."));
        assertTrue(getPv.contains("<cwmp:GetParameterValues>"));
        assertTrue(getPv.contains("Device.DeviceInfo."));

        // 3. SetParameterValues
        Map<String, String> sets = new HashMap<>();
        sets.put("Device.WiFi.SSID.1.SSID", "MyHomeWiFi");
        String spv = CwmpSoapBuilder.buildSetParameterValues(sets);
        assertTrue(spv.contains("<cwmp:SetParameterValues>"));
        assertTrue(spv.contains("MyHomeWiFi"));

        // 4. Reboot
        String reboot = CwmpSoapBuilder.buildReboot("TEST_CMD");
        assertTrue(reboot.contains("<cwmp:Reboot>"));
        assertTrue(reboot.contains("TEST_CMD"));

        // 5. Download
        String download = CwmpSoapBuilder.buildDownload("1 Firmware Upgrade Image", "http://server/firmware.bin", "admin", "123", 1024000L, "");
        assertTrue(download.contains("<cwmp:Download>"));
        assertTrue(download.contains("http://server/firmware.bin"));
    }

}
