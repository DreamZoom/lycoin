package com.ying.cloud.lycoin.net;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.DefaultHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LycoinHttpHandler extends DefaultHandler {


    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        //super.handle(target, baseRequest, request, response);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("hello");
    }
}
