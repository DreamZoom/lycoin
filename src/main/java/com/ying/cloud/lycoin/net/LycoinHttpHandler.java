package com.ying.cloud.lycoin.net;

import com.google.gson.Gson;
import com.ying.cloud.lycoin.LycoinApplicationContext;
import com.ying.cloud.lycoin.models.BlockChain;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LycoinHttpHandler extends AbstractHandler {

    LycoinApplicationContext context;

    public LycoinHttpHandler(LycoinApplicationContext context){
        this.context = context;
    }


    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        //super.handle(target, baseRequest, request, response);
        response.setStatus(HttpServletResponse.SC_OK);

        String action = request.getParameter("action");
        if("blocks".equals(action)){
            BlockChain chain = context.getChain();
            Gson gson=new Gson();
            response.getWriter().write(gson.toJson(chain));
        }
        baseRequest.setHandled(true);
    }
}
