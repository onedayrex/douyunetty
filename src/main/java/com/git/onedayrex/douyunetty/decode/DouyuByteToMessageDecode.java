package com.git.onedayrex.douyunetty.decode;

import com.git.onedayrex.douyunetty.uitls.MessageUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class DouyuByteToMessageDecode extends ByteToMessageDecoder {
    private static final Logger log = LogManager.getLogger(DouyuByteToMessageDecode.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //头部=12字节，所以字节数大于12才处理
        if (in.readableBytes() > 12) {
            byte[] bytes = ByteBufUtil.getBytes(in);
            //i=12 直接跳过头部处理
            for (int i = 12; i < bytes.length; i++) {
                //如果当前字节等于0，则到包的末尾，扔到下一个解码器处理
                if (bytes[i] == 0) {
                    // 长度 = 当前index+1 - 头部(12) - 尾部(1)
                    int length = i + 1 - 12 - 1;
                    //bytebuf中跳过头部，不向下一个处理器扔数据
                    in.skipBytes(12);
                    //得到的内部为内容  总字节 = 头部(12) + 内容 + 尾部(1)
                    ByteBuf byteBuf = in.readBytes(length);
                    //跳过尾部
                    in.skipBytes(1);
                    out.add(byteBuf);
                    break;
                }
            }
        }
    }

    /**
     * 心跳发送连接
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (IdleState.WRITER_IDLE == idleStateEvent.state()) {
                ctx.writeAndFlush(MessageUtils.getSendBuf("type@=mrkl/"));
                log.info("===>发送心跳成功");
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
