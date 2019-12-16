package com.ying.cloud.lycoin;

import com.google.gson.Gson;
import com.ying.cloud.lycoin.miner.Miner;
import com.ying.cloud.lycoin.net.Source;
import com.ying.cloud.lycoin.net.netty.NettyClientNode;
import com.ying.cloud.lycoin.transaction.AuthorizationInfo;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class HttpApiServer {

    Miner miner;

    public NettyClientNode getClientNode() {
        return clientNode;
    }

    public void setClientNode(NettyClientNode clientNode) {
        this.clientNode = clientNode;
    }

    private NettyClientNode clientNode;

    public BraftNettyNode getMy() {
        return my;
    }

    public void setMy(BraftNettyNode my) {
        this.my = my;
    }

    private BraftNettyNode my;
    public HttpApiServer(Miner miner){
        this.miner = miner;
    }
    public void run(){
        run(18188);
    }


    public void run(int port){
        Server server = new Server(port);
        server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize",-1);
        server.setAttribute("org.eclipse.jetty.LEVEL","INFO");
        server.setAttribute("encoding","UTF-8");
        server.setAttribute("forceEncoding","true");
        Handler handler =new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                String action = request.getParameter("action");
                response.addHeader("content-type","text/html; charset=utf-8");
                if("auth".equals(action)){

                    try{
                        AuthorizationInfo authorizationInfo =mapper(AuthorizationInfo.class,request);
                        authorizationInfo.setNonce(System.currentTimeMillis());
                        authorizationInfo.setId(authorizationInfo.toHashString());
                        miner.accept(authorizationInfo);

                    }
                    catch (Exception error){
                        response.getWriter().write(error.getMessage());
                    }

                }

                if("blocks".equals(action)){
                    Gson gson=new Gson();

                    response.getWriter().write(gson.toJson(miner.getChain().getBlocks()));
                }

                if("nodes".equals(action)){
                    Gson gson=new Gson();
                    List<Source> sources =new ArrayList<>();
                    sources.add(my);
                    clientNode.getSources().forEach((s)->{
                        BraftNettyNode n = (BraftNettyNode)s;
                        sources.add(new BraftNettyNode(n.host,n.port));
                    });
                    response.getWriter().write(gson.toJson(sources));
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
