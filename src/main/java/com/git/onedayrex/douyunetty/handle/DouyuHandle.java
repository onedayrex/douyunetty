package com.git.onedayrex.douyunetty.handle;

import com.git.onedayrex.douyunetty.uitls.MessageUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class DouyuHandle extends ChannelInboundHandlerAdapter {
    private static final Logger log = LogManager.getLogger(DouyuHandle.class);

    public static final String LOGIN_FLAG = "loginres";

    public static final String JOIN_FLAG = "pingreq";

    public static final String CHAT_FLAG = "chatmsg";

    public final String roomId;

    public DouyuHandle(String roomId) {
        this.roomId = roomId;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String loginCMD = "type@=loginreq/roomid@=" + roomId + "/";
        ctx.writeAndFlush(MessageUtils.getSendBuf(loginCMD));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Map<String,String> result = (Map<String,String>) msg;
        if (result != null) {
            String type = result.get("type");
            if (LOGIN_FLAG.equals(type)) {
                String joinGroupCMD =  "type@=joingroup/rid@=9999/gid@=-9999/";
                ctx.writeAndFlush(MessageUtils.getSendBuf(joinGroupCMD));
                log.info("==>登录成功，加入弹幕群组");
                return;
            } else if (JOIN_FLAG.equals(type)) {
                log.info("==>加入弹幕群组成功");
            }else if(CHAT_FLAG.equals(type)){
                String userName = result.get("nn");
                String level = result.get("level");
                String txt = result.get("txt");
                log.info(userName + "(" + level + ")" + "==>" + txt);
            }else {
                String userName = result.get("nn");
//                log.info(userName + ",消息类型==>" + type);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
