package cn.iocoder.yudao.module.media.onvif.client;

import cn.iocoder.yudao.module.media.onvif.bean.OnvifDevice;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ONVIF SOAP 协议交互客户端
 * 支持 WS-Discovery 设备发现、SOAP 摘要鉴权、Profile获取、RTSP取流地址以及云台控制
 *
 * @author MzMedia
 */
@Slf4j
@Component
public class OnvifSoapClient {

    private static final String WS_DISCOVERY_PROBE =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<e:Envelope xmlns:e=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
            "            xmlns:w=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\"\n" +
            "            xmlns:d=\"http://schemas.xmlsoap.org/ws/2005/04/discovery\"\n" +
            "            xmlns:dn=\"http://www.onvif.org/ver10/network/wsdl\">\n" +
            "  <e:Header>\n" +
            "    <w:MessageID>uuid:%s</w:MessageID>\n" +
            "    <w:To>urn:schemas-xmlsoap-org:ws:2005:04:discovery</w:To>\n" +
            "    <w:Action>http://schemas.xmlsoap.org/ws/2005/04/discovery/Probe</w:Action>\n" +
            "  </e:Header>\n" +
            "  <e:Body>\n" +
            "    <d:Probe>\n" +
            "      <d:Types>dn:NetworkVideoTransmitter</d:Types>\n" +
            "    </d:Probe>\n" +
            "  </e:Body>\n" +
            "</e:Envelope>";

    private final OkHttpClient httpClient;

    public OnvifSoapClient() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(8, TimeUnit.SECONDS)
                .writeTimeout(8, TimeUnit.SECONDS)
                .build();
    }

    /**
     * WS-Discovery 局域网组播搜索 ONVIF 设备
     *
     * @param timeoutMs 搜索超时时间（毫秒）
     * @return 搜索到的设备列表
     */
    public List<OnvifDevice> discoverDevices(int timeoutMs) {
        List<OnvifDevice> list = new ArrayList<>();
        Set<String> discoveredIps = new HashSet<>();
        DatagramSocket socket = null;

        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(timeoutMs > 0 ? timeoutMs : 3000);
            socket.setBroadcast(true);

            String probe = String.format(WS_DISCOVERY_PROBE, UUID.randomUUID());
            byte[] sendData = probe.getBytes(StandardCharsets.UTF_8);
            InetAddress multicastAddr = InetAddress.getByName("239.255.255.250");
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, multicastAddr, 3702);
            socket.send(sendPacket);

            long endTime = System.currentTimeMillis() + socket.getSoTimeout();
            byte[] recvBuf = new byte[8192];

            while (System.currentTimeMillis() < endTime) {
                try {
                    DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
                    socket.receive(recvPacket);
                    String resp = new String(recvPacket.getData(), 0, recvPacket.getLength(), StandardCharsets.UTF_8);

                    String xaddr = extractTagValue(resp, "XAddrs");
                    if (xaddr == null) {
                        xaddr = extractTagValue(resp, "d:XAddrs");
                    }
                    if (xaddr != null && !xaddr.isEmpty()) {
                        String[] urls = xaddr.trim().split("\\s+");
                        for (String u : urls) {
                            try {
                                URL parsed = new URL(u);
                                String ip = parsed.getHost();
                                int port = parsed.getPort() > 0 ? parsed.getPort() : 80;
                                if (!discoveredIps.contains(ip + ":" + port)) {
                                    discoveredIps.add(ip + ":" + port);
                                    OnvifDevice dev = OnvifDevice.builder()
                                            .name("ONVIF_" + ip)
                                            .ip(ip)
                                            .port(port)
                                            .username("admin")
                                            .password("admin123")
                                            .status(1)
                                            .build();
                                    list.add(dev);
                                }
                            } catch (Exception ignore) {}
                        }
                    }
                } catch (SocketTimeoutException e) {
                    break;
                }
            }
        } catch (Exception e) {
            log.warn("[ONVIF Discovery] 探测发生异常: {}", e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
        return list;
    }

    /**
     * 获取设备硬件与厂商信息
     */
    public boolean fetchDeviceInfo(OnvifDevice device) {
        String soapXml = buildSoapEnvelope(device,
                "<GetDeviceInformation xmlns=\"http://www.onvif.org/ver10/device/wsdl\"/>");
        String resp = postSoap(device, "/onvif/device_service", soapXml);
        if (resp == null) {
            return false;
        }
        String manufacturer = extractTagValue(resp, "Manufacturer");
        String model = extractTagValue(resp, "Model");
        String serialNumber = extractTagValue(resp, "SerialNumber");
        String firmware = extractTagValue(resp, "FirmwareVersion");

        if (manufacturer != null) device.setManufacturer(manufacturer);
        if (model != null) device.setModel(model);
        if (serialNumber != null) device.setSerialNumber(serialNumber);
        if (firmware != null) device.setFirmware(firmware);
        return true;
    }

    /**
     * 获取媒体 Profile 列表
     */
    public List<String> getProfiles(OnvifDevice device) {
        String soapXml = buildSoapEnvelope(device,
                "<GetProfiles xmlns=\"http://www.onvif.org/ver10/media/wsdl\"/>");
        String resp = postSoap(device, "/onvif/Media", soapXml);
        if (resp == null) {
            resp = postSoap(device, "/onvif/media_service", soapXml);
        }
        List<String> profiles = new ArrayList<>();
        if (resp != null) {
            Matcher m = Pattern.compile("token=[\"']([^\"']+)[\"']").matcher(resp);
            while (m.find()) {
                profiles.add(m.group(1));
            }
        }
        return profiles;
    }

    /**
     * 获取指定 Profile 的 RTSP 取流地址
     */
    public String getStreamUri(OnvifDevice device, String profileToken) {
        String token = (profileToken != null && !profileToken.isEmpty()) ? profileToken : "Profile_1";
        String soapXml = buildSoapEnvelope(device,
                "<GetStreamUri xmlns=\"http://www.onvif.org/ver10/media/wsdl\">\n" +
                "  <StreamSetup>\n" +
                "    <Stream xmlns=\"http://www.onvif.org/ver10/schema\">RTP-Unicast</Stream>\n" +
                "    <Transport xmlns=\"http://www.onvif.org/ver10/schema\">\n" +
                "      <Protocol>RTSP</Protocol>\n" +
                "    </Transport>\n" +
                "  </StreamSetup>\n" +
                "  <ProfileToken>" + token + "</ProfileToken>\n" +
                "</GetStreamUri>");
        String resp = postSoap(device, "/onvif/Media", soapXml);
        if (resp == null) {
            resp = postSoap(device, "/onvif/media_service", soapXml);
        }
        if (resp != null) {
            String uri = extractTagValue(resp, "Uri");
            if (uri != null && !uri.isEmpty()) {
                // 如果返回的 RTSP 缺少用户名密码，主动补齐
                if (!uri.contains("@") && device.getUsername() != null && device.getPassword() != null) {
                    uri = uri.replace("rtsp://", "rtsp://" + device.getUsername() + ":" + device.getPassword() + "@");
                }
                return uri;
            }
        }
        // 兜底返回基于规范的常规 RTSP 地址
        return "rtsp://" + device.getUsername() + ":" + device.getPassword() + "@" + device.getIp() + ":554/live/ch0";
    }

    /**
     * 云台控制: 持续移动 ContinuousMove
     * pan: -1.0 到 1.0 (左到右)
     * tilt: -1.0 到 1.0 (下到上)
     * zoom: -1.0 到 1.0 (缩放到放大)
     */
    public boolean ptzContinuousMove(OnvifDevice device, String profileToken, double pan, double tilt, double zoom) {
        String token = (profileToken != null && !profileToken.isEmpty()) ? profileToken : "Profile_1";
        String soapXml = buildSoapEnvelope(device,
                "<ContinuousMove xmlns=\"http://www.onvif.org/ver20/ptz/wsdl\">\n" +
                "  <ProfileToken>" + token + "</ProfileToken>\n" +
                "  <Velocity>\n" +
                "    <PanTilt x=\"" + pan + "\" y=\"" + tilt + "\" xmlns=\"http://www.onvif.org/ver10/schema\"/>\n" +
                "    <Zoom x=\"" + zoom + "\" xmlns=\"http://www.onvif.org/ver10/schema\"/>\n" +
                "  </Velocity>\n" +
                "</ContinuousMove>");
        String resp = postSoap(device, "/onvif/PTZ", soapXml);
        if (resp == null) {
            resp = postSoap(device, "/onvif/ptz_service", soapXml);
        }
        return resp != null;
    }

    /**
     * 云台控制: 停止当前运动 Stop
     */
    public boolean ptzStop(OnvifDevice device, String profileToken) {
        String token = (profileToken != null && !profileToken.isEmpty()) ? profileToken : "Profile_1";
        String soapXml = buildSoapEnvelope(device,
                "<Stop xmlns=\"http://www.onvif.org/ver20/ptz/wsdl\">\n" +
                "  <ProfileToken>" + token + "</ProfileToken>\n" +
                "  <PanTilt>true</PanTilt>\n" +
                "  <Zoom>true</Zoom>\n" +
                "</Stop>");
        String resp = postSoap(device, "/onvif/PTZ", soapXml);
        if (resp == null) {
            resp = postSoap(device, "/onvif/ptz_service", soapXml);
        }
        return resp != null;
    }

    /**
     * 调用预置位 GotoPreset
     */
    public boolean presetGoto(OnvifDevice device, String profileToken, String presetToken) {
        String token = (profileToken != null && !profileToken.isEmpty()) ? profileToken : "Profile_1";
        String soapXml = buildSoapEnvelope(device,
                "<GotoPreset xmlns=\"http://www.onvif.org/ver20/ptz/wsdl\">\n" +
                "  <ProfileToken>" + token + "</ProfileToken>\n" +
                "  <PresetToken>" + presetToken + "</PresetToken>\n" +
                "</GotoPreset>");
        String resp = postSoap(device, "/onvif/PTZ", soapXml);
        return resp != null;
    }

    /**
     * 发送 SOAP POST 请求
     */
    private String postSoap(OnvifDevice device, String servicePath, String xmlPayload) {
        String url = "http://" + device.getIp() + ":" + device.getPort() + servicePath;
        RequestBody body = RequestBody.create(xmlPayload, MediaType.parse("application/soap+xml; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                log.warn("[ONVIF SOAP] 请求失败 URL: {}, 状态码: {}", url, response.code());
            }
        } catch (IOException e) {
            log.warn("[ONVIF SOAP] 连接异常 {}: {}", url, e.getMessage());
        }
        return null;
    }

    /**
     * 构建带有 WS-Security UsernameToken Digest 的 SOAP 报文头
     */
    private String buildSoapEnvelope(OnvifDevice device, String bodyContent) {
        String nonceStr = UUID.randomUUID().toString().replace("-", "");
        byte[] nonce = nonceStr.getBytes(StandardCharsets.UTF_8);
        String created = getUtcTimestamp();

        String passwordDigest = "";
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            sha1.update(nonce);
            sha1.update(created.getBytes(StandardCharsets.UTF_8));
            sha1.update((device.getPassword() != null ? device.getPassword() : "").getBytes(StandardCharsets.UTF_8));
            passwordDigest = Base64.getEncoder().encodeToString(sha1.digest());
        } catch (Exception ignore) {}

        String nonceBase64 = Base64.getEncoder().encodeToString(nonce);
        String username = device.getUsername() != null ? device.getUsername() : "admin";

        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
                "               xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\"\n" +
                "               xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">\n" +
                "  <soap:Header>\n" +
                "    <wsse:Security soap:mustUnderstand=\"true\">\n" +
                "      <wsse:UsernameToken>\n" +
                "        <wsse:Username>" + username + "</wsse:Username>\n" +
                "        <wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest\">" + passwordDigest + "</wsse:Password>\n" +
                "        <wsse:Nonce EncodingType=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary\">" + nonceBase64 + "</wsse:Nonce>\n" +
                "        <wsu:Created>" + created + "</wsu:Created>\n" +
                "      </wsse:UsernameToken>\n" +
                "    </wsse:Security>\n" +
                "  </soap:Header>\n" +
                "  <soap:Body>\n" +
                bodyContent + "\n" +
                "  </soap:Body>\n" +
                "</soap:Envelope>";
    }

    private String getUtcTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    private String extractTagValue(String xml, String tagName) {
        if (xml == null || xml.isEmpty()) return null;
        Pattern pattern = Pattern.compile("<(?:\\w+:)?(" + tagName + ")[^>]*>([^<]+)</(?:\\w+:)?\\1>");
        Matcher matcher = pattern.matcher(xml);
        if (matcher.find()) {
            return matcher.group(2).trim();
        }
        return null;
    }
}
