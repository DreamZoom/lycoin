package com.ying.cloud.lycoin.net.http;


import com.ying.cloud.lycoin.LycoinContext;
import com.ying.cloud.lycoin.net.Message;
import com.ying.cloud.lycoin.net.*;
import com.ying.cloud.lycoin.utils.ByteArrayUtils;
import com.ying.cloud.lycoin.utils.HttpUtils;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class HttpNetwork extends PeerNode<HttpSource> {


    LycoinContext context;
    public HttpNetwork(LycoinContext context){
        this.context = context;
    }

    @Override
    public void setup(){


        Server server = new Server(context.getConfig().getHttpPort());
        server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize",-1);
        Handler httpHandler =new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
               String action = request.getParameter("action");
               if("message".equals(action)){
                   String msg = request.getParameter("message");
                   byte[] bytes = org.apache.commons.codec.binary.Base64.decodeBase64(msg);
                   Message message =  ByteArrayUtils.decode(bytes);
                   HttpSource source =new HttpSource("",1133);
                   handler.handle(source,message);

                   response.getWriter().write("ok");
               }
               baseRequest.setHandled(true);
            }
        };



        server.setHandler(httpHandler);
        try{
            server.start();
        }
        catch (Exception err){
            System.out.println(err.getMessage());
        }
    }

    @Override
    public void send(HttpSource source, Message message) {

        byte[] bytes = ByteArrayUtils.encode(message);
        String msg = org.apache.commons.codec.binary.Base64.encodeBase64String(bytes);
        String url  = "http://"+source.getHost()+":"+source.getMyPort()+"?action=message";
        Map<String,String> data = new HashMap<>();
        data.put("message",msg);
        HttpUtils.doPost(url,data);
    }


}
