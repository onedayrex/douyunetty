package com.git.onedayrex.douyunetty.uitls;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.UnsupportedEncodingException;

public class MessageUtils {

    public static final ByteBuf getSendBuf(String msg) {
        int contentLength = 4 + 4 + msg.length() + 1;
        ByteBuf buffer = Unpooled.buffer(contentLength);
        byte[] msgBytes = new byte[0];
        try {
            msgBytes = msg.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        buffer.writeIntLE(contentLength);
        buffer.writeIntLE(contentLength);
        buffer.writeIntLE(689);
        buffer.writeBytes(msgBytes);
        buffer.writeByte(0);
        return buffer;
    }
}
