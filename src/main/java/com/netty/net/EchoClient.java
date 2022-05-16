package com.netty.net;

import com.netty.util.EchoUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

public class EchoClient {

    public org.slf4j.Logger logger = LoggerFactory.getLogger(EchoClient.class);

    private final int READ_TIMEOUT = 75;

    EchoUtil echoUtil = new EchoUtil();
    private final Map<String, Object> map;

    private final String sendIp;
    private final int sendPort;
    private final String webIp;
    private final String sendWebUrl;

    public EchoClient(Map<String, Object> map) {
        this.map = map;
        this.sendIp = String.valueOf(map.get("sendIp"));
        this.sendPort = Integer.parseInt(String.valueOf(map.get("sendPort")));
        this.webIp = String.valueOf(map.get("webIp"));
        this.sendWebUrl = String.valueOf(map.get("sendWebUrl"));
    }

    public void dataSearch(String mdcKey){

        MDC.put("logClass", mdcKey);
        String sendData;

        while(true) {

            try {

                // sendData를 보내면 웹에서 응답으로 데이터가 있으면 데이터를 없으면 NoData를 보냄
                sendData = echoUtil.refineWebConnect1(webIp, sendWebUrl, "sendData".getBytes());

            } catch(Exception e){

                logger.error("[ERROR] web connect=====");
                logger.error(e.getMessage());
                sendData = "WebConnectError";
            }

            try{

                // 데이터가 없으면 발송 하지 않고 데이터가 있으면 발송함
                if(sendData == null || sendData.equals("NoData") || sendData.trim().equals("") || sendData.trim().equals(" ")){

                    // log4j.debug("보낼 데이터 없음 ");

                } else if(sendData.length() > 100){

                    logger.debug("================================================================================");
                    logger.debug("==================================전문 발송 시작 ==================================");

                    map.put("sendData", sendData);

                    //실제 발송 부분
                    run(mdcKey);

                }

                //3초마다
                Thread.sleep(3000);

            } catch(Exception e){
                logger.debug("ClientError!!!!" + e.toString());
            }
        }
    }

    public void run(String mdcKey) {

        MDC.put("logClass", mdcKey);

        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();

        try {

            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch){
                            ChannelPipeline p = ch.pipeline();
                            p.addLast("readTimeoutHandler", new ReadTimeoutHandler(READ_TIMEOUT));
                            p.addLast(new EchoClientHandler(map, logger, mdcKey));

                        }
                    });

            // Start the client.
            ChannelFuture f = b.connect(sendIp, sendPort).sync();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
            logger.debug("Error == "+e.toString());
        } finally {

            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }
}
