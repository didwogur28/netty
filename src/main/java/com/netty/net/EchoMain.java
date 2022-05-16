package com.netty.net;

import com.netty.logClass.ClientLog;
import com.netty.logClass.ServerLog;
import com.netty.util.EchoUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class EchoMain {

    EchoUtil echoUtil = new EchoUtil();

    private String echoKind;

    public void start(String[] args) throws Exception {

        Map<String, Object> infoMap = new HashMap<>();

        echoKind = args[0];

        infoMap.put("echoKind", echoKind);

        echoUtil.getInfoMap(infoMap);

        if("Server".equals(echoKind)){

            new ServerLog(infoMap, "ServerLog").run("ServerLog");
        }

        if("Client".equals(echoKind)){

            new ClientLog(infoMap, "ClientLog").dataSearch("ClientLog");

        }

    }
}
