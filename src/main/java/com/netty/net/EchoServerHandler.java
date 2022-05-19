package com.netty.net;

import com.netty.util.EchoUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    public org.slf4j.Logger logger = LoggerFactory.getLogger(EchoServerHandler.class);

    EchoUtil echoUtil = new EchoUtil();

    private boolean isFirst = true;

    private ByteBuf readData;
    private String letterLength;

    private String webIp;
    private String recvWebUrl;

    public EchoServerHandler(Map<String, Object> map, org.slf4j.Logger logger, String mdcKey) {

        this.webIp = (String) map.get("webIp");
        this.recvWebUrl = (String) map.get("recvWebUrl");

        this.logger = logger;

        MDC.put("logClass", mdcKey);

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        // 전문 수신부
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        // 전문 수신 에러시

        cause.printStackTrace();
        logger.debug("exception : "+cause.toString());
        ctx.close();
    }
}
