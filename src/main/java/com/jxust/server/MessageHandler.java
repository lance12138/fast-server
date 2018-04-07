package com.jxust.server;

import com.jxust.protobuf.FastMessage;
import com.jxust.server.biz.UserManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.jxust.protobuf.FastMessage.*;
@ChannelHandler.Sharable
@Component
public class MessageHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    private UserManager userManager;
    private FastServer server;

    public MessageHandler(FastServer server,UserManager userManager) {
        this.server = server;
        this.userManager = userManager;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof LoginReq) {
            LoginReq loginReq = (LoginReq)msg;
            logger.info("get msg:loginReq,login user:{}",loginReq.getChatId());

            FastUser user = new FastUser();
            user.setId(loginReq.getChatId());
            userManager.onUserLogin(user,ctx.channel());
            FastRes.Builder builder = FastRes.newBuilder();
            builder.setChatId(loginReq.getChatId());
            builder.setCode(0);
            builder.setMessage("login success!");
            ctx.channel().writeAndFlush(builder.build());
        }else if(msg instanceof DirectChatReq) {
            DirectChatReq chatReq = (DirectChatReq) msg;
            FastUser user = new FastUser();
            user.setId(chatReq.getTargetChatId());
            userManager.communicate(ctx.channel(),user,chatReq.getData());
        }
    }
}
