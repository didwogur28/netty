package com.netty.logClass;

import com.netty.net.EchoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Map;

public class ClientLog extends EchoClient {

    public org.slf4j.Logger logger = LoggerFactory.getLogger(ClientLog.class);

    public ClientLog(Map<String, Object> infoMap, String mdcKey) {

        super(infoMap);

        MDC.put("logClass", mdcKey);

        logger.debug("===================================== Tcp/Ip ClientLog 시작 =====================================");
        logger.debug("=tcp/ip 통신 sendPort===================[" + infoMap.get("sendPort") + "]");
        logger.debug("=tcp/ip 통신 sendIp===================[" + infoMap.get("sendIp") + "]");
        logger.debug("=tcp/ip 통신 webIp===================[" + infoMap.get("webIp") + "]");
        logger.debug("=tcp/ip 통신 sendWebUrl===================[" + infoMap.get("sendWebUrl") + "]");
        logger.debug("===================================== Tcp/Ip ServerLog 시작 =====================================");
    }

    @Override
    public void dataSearch(String mdcKey){
        // TODO Auto-generated method stub
        super.dataSearch(mdcKey);
        MDC.remove("logClass");
        MDC.clear();
    }

}
