package com.git.onedayrex.douyunetty.decode;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 把文字消息解码成map封装
 */
public class DouyuMessageToMessageDecode extends MessageToMessageDecoder<String> {
    @Override
    protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
        if (msg != null) {
            Map<String, String> map = new HashMap<>();
            String[] keyAndValues = msg.split("/");
            for (String keyAndValue : keyAndValues) {
                String[] split = keyAndValue.split("@=");
                if (split.length < 2) {
                    map.put(split[0], "");
                }else {
                    map.put(split[0], split[1]);
                }
            }
            out.add(map);
        }
    }
}
