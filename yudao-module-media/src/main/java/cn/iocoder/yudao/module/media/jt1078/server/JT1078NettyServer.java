package cn.iocoder.yudao.module.media.jt1078.server;

import cn.iocoder.yudao.module.media.jt1078.codec.JT1078FrameDecoder;
import cn.iocoder.yudao.module.media.jt1078.codec.JT1078Message;
import cn.iocoder.yudao.module.media.jt1078.config.JT1078Config;
import cn.iocoder.yudao.module.media.jt1078.session.JT1078SessionManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * JT/T 1078 车载音视频 Netty 服务端
 *
 * @author MzMedia
 */
@Slf4j
@Component
public class JT1078NettyServer {

    @Autowired
    private JT1078Config config;

    @Autowired
    private JT1078SessionManager sessionManager;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture serverChannelFuture;

    @PostConstruct
    public void start() {
        if (!config.isEnable()) {
            log.info("[JT1078 Server] 服务已配置为禁用，跳过端口监听");
            return;
        }

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast("decoder", new JT1078FrameDecoder());
                        ch.pipeline().addLast("handler", new SimpleChannelInboundHandler<JT1078Message>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, JT1078Message msg) {
                                sessionManager.registerChannel(msg.getSimNo(), ctx.channel());
                                sessionManager.onMessageReceived(msg);
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) {
                                sessionManager.removeChannel(ctx.channel());
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                log.warn("[JT1078 Handler] 连接异常: {}", cause.getMessage());
                                ctx.close();
                            }
                        });
                    }
                });

        try {
            serverChannelFuture = b.bind(config.getPort()).sync();
            log.info("[JT1078 Server] 车载音视频服务端成功启动，监听端口: {}", config.getPort());
        } catch (Exception e) {
            log.error("[JT1078 Server] 启动失败: {}", e.getMessage(), e);
        }
    }

    @PreDestroy
    public void stop() {
        if (serverChannelFuture != null) {
            serverChannelFuture.channel().close();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        log.info("[JT1078 Server] 服务已安全关闭");
    }
}
