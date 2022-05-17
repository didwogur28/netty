package com.netty.net;

import com.netty.util.EchoUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

public class EchoClientHandler extends ChannelInboundHandlerAdapter {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(EchoClientHandler.class);

    EchoUtil echoUtil = new EchoUtil();

    private String webIp;
    private String sendWebUrl;

    private boolean isFirst = true;

    private String sendData;
    private ByteBuf readData = null;

    public EchoClientHandler(Map<String, Object> map, org.slf4j.Logger logger, String mdcKey) {

        this.logger = logger;

        this.webIp = (String) map.get("webIp");
        this.sendWebUrl = (String) map.get("sendWebUrl");

        this.sendData = (String) map.get("sendData");
        MDC.put("logClass", mdcKey);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {

        logger.debug("sendData =======["+ new String(sendData.getBytes()) + "]");
        logger.debug("sendData length=======["+ sendData.getBytes().length + "]");

        //웹을 호출하여 발송할 데이터 가져와서 발송함
        ctx.writeAndFlush(Unpooled.wrappedBuffer(sendData.getBytes()));

    }
}
