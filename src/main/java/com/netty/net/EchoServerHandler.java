package com.netty.net;

import com.netty.util.EchoUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
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

        if (isFirst) {

            logger.debug("============================================= 전문 처리 시작 =============================================");
            logger.debug("BEGIN");

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

            logger.debug("recvRawPacket§[" + new String(message) + "]");
            logger.debug("수신 받은값 길이======[" + readData.readableBytes() + "]");

            String ackData;

            try {

                ackData = echoUtil.webConnect(webIp, recvWebUrl, readData);

            } catch(Exception ex) {

                ackData ="0001 ";
                logger.error("[ERROR] web connect=====");
                logger.error(ex.getMessage());

            }

            logger.debug("===========리파인웹 연결 끝===========");

            logger.debug("recvWebPacket§["+ackData+"]");
            logger.debug("웹에서 받은값 길이===="+ackData.getBytes().length);

            //ackData의 byte를  포장하여 전송함
            ctx.writeAndFlush(Unpooled.wrappedBuffer(ackData.getBytes()));

            //초기화 함
            isFirst = true;
            readData.clear();
            letterLength = null;

            logger.debug("==================================전문 발송 종료 ==================================");
            logger.debug("================================================================================");

            if (!ackData.equals("0001")) {

                //Log에 응답정보 셋팅
                byte[] ack = null;
                byte[] letterType = null;
                byte[] bankCtrlNo = null;

                String ackString = new String(ack);

                logger.debug("recvAck§["+ ackString +"]");
                logger.debug("recvLetterType§["+ new String(letterType) +"]");

                if((ackString.equals("000") || ackString.equals("P000"))) {
                    logger.debug("[Normal] recvWebPacket 응답이 정상");
                } else{
                    logger.debug("[Error] 웹에서 받은값 응답이 000 정상이 아님");
                }

                ctx.close();

                logger.debug("Channel out");
                logger.debug("END");
                logger.debug("==========================================================================================");

            }

        } else if(Integer.parseInt(letterLength) < (totReadDataLangth)) {

            logger.debug("======== [Error] overflow letter size ========");

            byte[] messageOverflow = new byte[readData.readableBytes()];
            int readerIndexOverflow = readData.readerIndex();

            readData.getBytes(readerIndexOverflow, messageOverflow);

            logger.debug("전체 길이 5바이트==={}",Integer.parseInt(letterLength));
            logger.debug("overflow 수신 받은값 =========[" + new String(messageOverflow) + "]");
            logger.debug("overflow 수신 받은값 길이======[" + readData.readableBytes() + "]");

            ctx.close();

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        cause.printStackTrace();
        logger.debug("exception : "+cause.toString());
        ctx.close();

    }
}
