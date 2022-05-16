package com.netty.net;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.LoggerFactory;

import java.util.Map;

@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    public org.slf4j.Logger logger = LoggerFactory.getLogger(EchoServerHandler.class);

    public EchoServerHandler(Map<String, Object> map, org.slf4j.Logger logger, String mdcKey) {

    }

}
