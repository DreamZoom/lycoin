package com.ying.cloud.lycoin.net;

import com.ying.cloud.lycoin.LycoinApplicationContext;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;

public class LycoinHttpServer {

    LycoinApplicationContext context;
    public LycoinHttpServer(LycoinApplicationContext context){
        this.context = context;
    }

    public void run(){
        Server server = new Server(context.getConfig().getHttpPort());
        Handler handler =new LycoinHttpHandler(context);
        server.setHandler(handler);
        try{
            server.start();
        }
        catch (Exception err){
            System.out.println(err.getMessage());
        }
    }
}
