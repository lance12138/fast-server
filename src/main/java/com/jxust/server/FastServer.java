package com.jxust.server;

import com.jxust.codec.FastDecoder;
import com.jxust.codec.FastEncoder;
import com.jxust.server.biz.UserManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FastServer {

    private static final Logger logger = LoggerFactory.getLogger(FastServer.class);
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Map<FastUser,Channel> userMap = new ConcurrentHashMap<>();
    private Map<Channel,FastUser> channelUserMap = new ConcurrentHashMap<>();
    @Value("${fast.server.port:8899}")
    private int port;
    private volatile boolean started;
    private Channel channel;
    private FastServer server;
    @Autowired
    private UserManager userManager;


    public void start() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY,true)
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .localAddress(port);

       server=this;
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new FastDecoder())
                        .addLast(new FastEncoder())
                        .addLast(new MessageHandler(server,userManager));
            }
        });

        ChannelFuture future = serverBootstrap.bind();
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if(channelFuture.isSuccess()) {
                    started=true;
                    channel = channelFuture.channel();
                    logger.info("fastServer is started success on:{}",port);
                } else {
                    stop();
                    throw new Exception("server bind port: "+port+" started failed,message: "+channelFuture);
                }
            }
        });

        started=true;
    }


    public void stop(){
        if(channel!=null&& channel.isOpen()) {
            channel.close();
        }

        if(bossGroup!=null) {
            bossGroup.shutdownGracefully();
        }

        if(workerGroup!=null) {
            workerGroup.shutdownGracefully();
        }

        started=false;
    }
    public Map<FastUser, Channel> getUserMap() {
        return userMap;
    }

    public void setUserMap(Map<FastUser, Channel> userMap) {
        this.userMap = userMap;
    }

    public Map<Channel, FastUser> getChannelUserMap() {
        return channelUserMap;
    }

    public void setChannelUserMap(Map<Channel, FastUser> channelUserMap) {
        this.channelUserMap = channelUserMap;
    }
}
