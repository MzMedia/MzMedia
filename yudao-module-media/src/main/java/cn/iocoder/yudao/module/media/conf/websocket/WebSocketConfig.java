package cn.iocoder.yudao.module.media.conf.websocket;

import cn.iocoder.yudao.module.media.conf.trace.TraceChannel;
import cn.iocoder.yudao.module.media.conf.webLog.LogChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        ServerEndpointExporter endpointExporter = new ServerEndpointExporter();

        endpointExporter.setAnnotatedEndpointClasses(LogChannel.class, TraceChannel.class);

        return endpointExporter;
    }
}
