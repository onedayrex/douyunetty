package com.git.onedayrex.douyunetty.client;

import com.git.onedayrex.douyunetty.decode.DouyuByteToMessageDecode;
import com.git.onedayrex.douyunetty.decode.DouyuMessageToMessageDecode;
import com.git.onedayrex.douyunetty.handle.DouyuHandle;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class DouyuNettyClient {
    private static final String host = "openbarrage.douyutv.com";
    private static final int port = 8601;
    private final String roomId;

    public DouyuNettyClient(String roomId) {
        this.roomId = roomId;
    }

    public void start() {
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new IdleStateHandler(0, 30, 0, TimeUnit.SECONDS));
                            ch.pipeline().addLast(new DouyuByteToMessageDecode());
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new DouyuMessageToMessageDecode());
                            ch.pipeline().addLast(new DouyuHandle(roomId));
                        }
                    });
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        DouyuNettyClient douyuNettyClient = new DouyuNettyClient("1126960");
        douyuNettyClient.start();
    }
}
