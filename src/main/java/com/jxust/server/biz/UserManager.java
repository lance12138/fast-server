package com.jxust.server.biz;

import com.jxust.protobuf.FastMessage;
import com.jxust.server.FastUser;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class UserManager {

    private static final Logger logger = LoggerFactory.getLogger(UserManager.class);
    private Map<FastUser,Channel> userMap = new ConcurrentHashMap<>();
    private Map<Channel,FastUser> channelUserMap = new ConcurrentHashMap<>();

    public void onUserLogin(FastUser user,Channel channel) {
        Channel channel1 = userMap.get(user);
        if(channel1==null) {
            synchronized (user) {
                channel1 =userMap.get(user);
                if(channel1==null) {
                    onAddUser(user,channel);
                }
            }
        }
    }

    public void onUserLogout(FastUser user) {
        synchronized (user) {
            onRemoveUser(user);
        }
    }

    public void communicate(Channel source,FastUser target,String msg) {
        Channel channel = userMap.get(target);
        //Channel channel1 = userMap.get(source);
        FastMessage.FastRes.Builder builder = FastMessage.FastRes.newBuilder();
        if(channel==null) {
            logger.warn("target user:{} not online",target.getName());
            builder.setCode(-1);
            builder.setMessage("user not online");
            source.writeAndFlush(builder.build());
            return;
        }

        channel.writeAndFlush(msg);
        builder.setCode(0);
        builder.setMessage("success");
        source.writeAndFlush(builder.build());

    }
    private void onAddUser(FastUser user,Channel channel) {
        userMap.put(user,channel);
        channelUserMap.put(channel,user);
    }

    private void onRemoveUser(FastUser user) {
        userMap.remove(user);
        Iterator<Map.Entry<Channel, FastUser>> iterator =
                channelUserMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Channel, FastUser> next = iterator.next();
            if(next.getValue().equals(user)) {
                channelUserMap.remove(next.getKey());
            }
        }
    }

}
