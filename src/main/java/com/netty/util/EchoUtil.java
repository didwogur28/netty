package com.netty.util;

import com.netty.logClass.ServerLog;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class EchoUtil {

    public org.slf4j.Logger logger = LoggerFactory.getLogger(EchoUtil.class);

    public void getInfoMap(Map<String, Object> infoMap) {

        String filePath;
        filePath = "/resources/infoProperties.xml";

        String xmlInsures = "";
        Document doc = null;
        try {
            SAXBuilder builder = new SAXBuilder();

            File propertiesFile = new File(filePath);
            doc = builder.build(propertiesFile);

        } catch(Exception e) {
            logger.debug("ERROR!! " + e.toString());
        }

        Element root = doc.getRootElement();

        List elements = root.getChildren();

        Iterator itr1 = doc.getDescendants(new ElementFilter("runMode"));

        while(itr1.hasNext()) {
            Element e1 = (Element) itr1.next();
            Attribute at1 = e1.getAttribute("id");

            infoMap.put("sendIp", e1.getChild("sendIp").getValue());
            infoMap.put("sendPort", e1.getChild("sendPort").getValue());
            infoMap.put("recvPort", e1.getChild("recvPort").getValue());
            infoMap.put("webIp", e1.getChild("webIp").getValue());
            infoMap.put("recvWebUrl", e1.getChild("recvWebUrl").getValue());
            infoMap.put("sendWebUrl", e1.getChild("sendWebUrl").getValue());
        }
    }

    public String refineWebConnect(String urlPath1, String urlPath2, byte[] request) throws Exception{

        String rData = "";
        String totData= "";

        URL url = new URL(urlPath1.trim()+ urlPath2.trim());

        HttpURLConnection con = (HttpURLConnection)url.openConnection();

        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestMethod("POST");
        con.setConnectTimeout(15000);
        con.setReadTimeout(15000);

        OutputStream os = con.getOutputStream();

        //데이터 웹에 송신
        os.write(request);

        os.flush();
        os.close();

        BufferedReader br = new BufferedReader( new InputStreamReader( con.getInputStream() ));

        // 표준출력으로 한 라인씩 출력
        while( ( rData = br.readLine() ) != null ) {
            totData += rData;
        }

        br.close();
        con.disconnect();

        return totData;
    }
}
