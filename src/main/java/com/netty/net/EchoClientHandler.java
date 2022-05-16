package com.netty.net;

import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class EchoClientHandler extends ChannelInboundHandlerAdapter {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(EchoClientHandler.class);

    public EchoClientHandler(Map<String, Object> map, org.slf4j.Logger logger, String mdcKey) {

    }
}
