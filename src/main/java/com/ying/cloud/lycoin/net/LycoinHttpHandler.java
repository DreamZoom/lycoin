package com.ying.cloud.lycoin.net;

import com.google.gson.Gson;
import com.ying.cloud.lycoin.LycoinContext;
import com.ying.cloud.lycoin.message.MessageAuthorizationInfo;
import com.ying.cloud.lycoin.message.MessageTransaction;
import com.ying.cloud.lycoin.models.BlockChain;
import com.ying.cloud.lycoin.transaction.AuthorizationInfo;
import com.ying.cloud.lycoin.transaction.Transaction;
import com.ying.cloud.lycoin.transaction.TransactionOut;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.springframework.cglib.core.Converter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;

public class LycoinHttpHandler extends AbstractHandler {

    LycoinContext context;

    public LycoinHttpHandler(LycoinContext context){
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
            response.getWriter().write(gson.toJson(chain.getBlocks()));
        }

        if("tx".equals(action)){
            try{
                TransactionOut out =mapper(TransactionOut.class,request);
                Transaction transaction =context.getTransactions().tx(context.getAccount(),out);
                if(transaction!=null){
                    context.getNetwork().broadcast(new MessageTransaction(transaction));
                    response.getWriter().write("ok");
                }
                else{
                    response.getWriter().write("error");
                }
            }
            catch (Exception error){
                response.getWriter().write(error.getMessage());
            }

        }

        if("amount".equals(action)){
            Gson gson=new Gson();
            response.getWriter().write(gson.toJson(context.getTransactions().getUnspentTransactionOuts()));
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
