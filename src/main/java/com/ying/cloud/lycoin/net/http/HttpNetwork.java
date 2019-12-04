package com.ying.cloud.lycoin.net.http;


import com.ying.cloud.lycoin.LycoinContext;
import com.ying.cloud.lycoin.net.message.MessageAuthorizationInfo;
import com.ying.cloud.lycoin.net.*;
import com.ying.cloud.lycoin.transaction.AuthorizationInfo;
import com.ying.cloud.lycoin.utils.ByteArrayUtils;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;


public class HttpNetwork extends Network {


    LycoinContext context;
    public HttpNetwork(LycoinContext context){
        this.context = context;
    }

    @Override
    public void setup(){


        Server server = new Server(context.getConfig().getHttpPort());
        Handler handler =new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
               String action = request.getParameter("action");
               if("message".equals(action)){
                   String msg = request.getParameter("message");
                   byte[] bytes = org.apache.commons.codec.binary.Base64.decodeBase64(msg);
                   IMessage message =  ByteArrayUtils.decode(bytes);
                   new Thread(new Runnable() {
                       @Override
                       public void run() {
                           trigger(null,message);
                       }
                   }).start();

                   response.getWriter().write("ok");
               }

                if("auth".equals(action)){

                    try{
                        AuthorizationInfo authorizationInfo =mapper(AuthorizationInfo.class,request);
                        context.getTransactions().addTransaction(authorizationInfo);
                        context.getNetwork().broadcast(new MessageAuthorizationInfo(authorizationInfo));
                    }
                    catch (Exception error){
                        response.getWriter().write(error.getMessage());
                    }

                }

               baseRequest.setHandled(true);
            }
        };



        server.setHandler(handler);
        try{
            server.start();
        }
        catch (Exception err){
            System.out.println(err.getMessage());
        }
    }


    public <T> T mapper(Class<T> cls,HttpServletRequest request) throws Exception{
        Field[] fields = cls.getDeclaredFields();

        Object out = cls.newInstance();
        for (int i = 0; i <fields.length ; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            String name = field.getName();
            String value = request.getParameter(name);
            if(value != null){
                Object o = parser(field.getType(),value);
                field.set(out,o);
            }

        }
        return (T)out;
    }

    public Object parser(Class cls,String value){
        if(Integer.class.equals(cls)){
            return Integer.parseInt(value);
        }
        if(Long.class.equals(cls)){
            return Long.parseLong(value);
        }

        if(Boolean.class.equals(cls)){
            return Boolean.parseBoolean(value);
        }

        if(Double.class.equals(cls)){
            return Double.parseDouble(value);
        }

        if(Short.class.equals(cls)){
            return Short.parseShort(value);
        }

        if(Float.class.equals(cls)){
            return Float.parseFloat(value);
        }

        if(String.class.equals(cls)){
            return value;
        }
        return null;
    }


}
