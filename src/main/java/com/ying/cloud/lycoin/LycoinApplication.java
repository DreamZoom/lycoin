package com.ying.cloud.lycoin;

import com.google.gson.Gson;
import com.ying.cloud.lycoin.event.LycoinEvent;
import com.ying.cloud.lycoin.event.LycoinEventListener;
import com.ying.cloud.lycoin.event.LycoinEventManager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;

/**
 * Lycoin 币应用容器
 */
public class LycoinApplication {

    /**
     * context 上下文
     */
    private LycoinApplicationContext context;

    private static final String DEFAULT_CONFING_FILE  = "lycoin.conf";

    public  LycoinApplication(){
        context = new LycoinApplicationContext();
    }

    /**
     * 加载配置文件
     * @param context
     * @throws Exception
     */
    public void loadConfig(LycoinApplicationContext context) throws Exception{
        Gson gson = new Gson();
        String json =  FileUtils.readFileToString(new File(DEFAULT_CONFING_FILE),Charset.forName("utf-8"));
        LycoinConfig config=gson.fromJson(json,LycoinConfig.class);
        context.setConfig(config);
    }


    public void setupServer(LycoinApplicationContext context){

    }

    public void setupRpcServer(LycoinApplicationContext context){

    }

    public void setupHttpServer(LycoinApplicationContext context){

    }

    public void  initEventSystem(LycoinApplicationContext context){
        LycoinEventManager eventManager =new LycoinEventManager();
        context.setEventManager(eventManager);
    }


    public void run(){
        try {
            loadConfig(context);

            initEventSystem(context);

            setupServer(context);

            setupRpcServer(context);

            setupHttpServer(context);

            context.getEventManager().addEventListener(new LycoinEventListener(){
                @Override
                public String name() {
                    return "block";
                }

                @Override
                public void action(LycoinEvent event) {
                    System.out.println("find a block");
                }

            });

            context.getEventManager().addEventListener(new LycoinEventListener() {
                @Override
                public String name() {
                    return "chain";
                }

                @Override
                public void action(LycoinEvent event) {
                    System.out.println("find a chain");
                }
            });


            context.getEventManager().fireEvent("block");

        }catch (Exception error){
            System.out.println(error.getMessage());
        }

    }

}
