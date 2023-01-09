package com.netty.net;

import com.netty.util.EchoUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
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
    private String letterLength = null;

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

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if (isFirst) {

            logger.debug("======================================= 응답 처리 시작 ===============================================");

            readData = (ByteBuf) msg;
            readData.markWriterIndex();

            letterLength = readData.toString(0, 4, CharsetUtil.UTF_8);

            isFirst = false;

        } else {

            readData.resetWriterIndex();
            readData.writeBytes((ByteBuf) msg);
            readData.markWriterIndex();

        }

        int totReadDataLangth = 0;

        totReadDataLangth = readData.readableBytes() - 4;

        if (Integer.parseInt(letterLength) == (totReadDataLangth)) {

            byte[] message = new byte[readData.readableBytes()];
            int readerIndex = readData.readerIndex();
            readData.getBytes(readerIndex, message);

            logger.debug("응답 받은값 =========" + new String(message));
            logger.debug("응답 받은값 길이======" + readData.readableBytes());

            try{

                echoUtil.webConnect(webIp, sendWebUrl, readData);

            } catch(Exception e){

                logger.error("[ERROR] web connect=====");
                logger.error(e.getMessage());

            }

            logger.debug("===========웹 연결 끝===========");

            sendData = null;
            isFirst = true;

            readData.clear();
            readData = null;
            letterLength = null;

            logger.debug("==================================전문 발송 종료 ==================================");
            logger.debug("================================================================================");

            ctx.close();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        logger.debug("===========channelReadComplete ======");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        String ackCode = "777";

        logger.debug("Disconnecting due to no inbound traffic");
        logger.debug("===================[ERROR] EchoClient 실행시 응답이 안옴 ==========");
        logger.debug("===========응답 패킷만 변경하여 다시 웹에 응답으로 전송===========");

        byte[] sendDataPacket = sendData.getBytes();
        int packetLength = sendDataPacket.length;

        logger.debug("packetLength ========== ["+ packetLength +"]");
        logger.debug("sendDataPacket ========== ["+ new String(sendDataPacket) +"]");

        byte[] ackCodeByte = ackCode.getBytes();
        logger.debug("ackCodeByte ========== ["+ new String(ackCodeByte) +"]");

        int pckLn = 0;	// 패킷 길이 4byte packet length
        int ackSP = 0;	// 응답코드 start 위치 ack start position
        int ackEP = 0;	// 응답코드 end 위치 ack end position

        byte[] byteMesgFirst = echoUtil.subBytesBetweenAToB(sendDataPacket,pckLn,ackSP); //앞에 4바이트는 길이 이므로 제외
        byte[] byteMesgLast = echoUtil.subBytesLenAfter(sendDataPacket,ackEP);

        byte[] ackMesg = new byte[ byteMesgFirst.length + ackCodeByte.length + byteMesgLast.length];

        String sendAck = new String(ackMesg);
        logger.debug("ackMesg=======["+sendAck+"]");

        try{

            //받은값에서 응답코드만 변경하여 그대로 받은 byte를 웹에 전송함
            echoUtil.webConnect1(webIp, sendWebUrl, ackMesg);

        } catch(Exception e){

            logger.error("[ERROR] web connect=====");
            logger.error(e.getMessage());

        }

        logger.debug("채널 닫음");

        cause.printStackTrace();
        ctx.close();
    }
}
