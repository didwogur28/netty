package com.netty.logClass;

import com.netty.net.EchoServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Map;

public class ServerLog extends EchoServer {

    public org.slf4j.Logger logger = LoggerFactory.getLogger(ServerLog.class);

    public ServerLog(Map<String, Object> infoMap, String mdcKey) {

        super(infoMap);

        MDC.put("logClass", mdcKey);

        logger.debug("===================================== Tcp/Ip ServerLog 시작 =====================================");
        logger.debug("=tcp/ip 통신 recvPort===================[" + infoMap.get("recvPort") + "]");
        logger.debug("=tcp/ip 통신 recvWebIp===================[" + infoMap.get("webIp") + "]");
        logger.debug("=tcp/ip 통신 recvWebRecvUrl===================[" + infoMap.get("recvWebUrl") + "]");
        logger.debug("===================================== Tcp/Ip ServerLog 시작 =====================================");
    }

    @Override
    public void run(String mdcKey) throws InterruptedException {
        // TODO Auto-generated method stub
        super.run(mdcKey);
        MDC.remove("logClass");
        MDC.clear();
    }
}
