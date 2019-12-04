package com.ying.cloud.lycoin.net;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public abstract class Network implements INetwork {

    List<IMessageHandler> handlers;
    List<ISource> sources;

    public Network(){
        this.handlers =new ArrayList<>();
        this.sources =new ArrayList<>();
    }
    public synchronized void trigger(ISource source, IMessage message){
        for (int i = 0; i < handlers.size(); i++) {
            IMessageHandler handler = this.handlers.get(i);
            ParameterizedType type = (ParameterizedType)(handler.getClass().getGenericInterfaces()[0]);
            if(message.getClass().equals(type.getActualTypeArguments()[0])){
                handler.handle(source,message);
            }
        }
    }

    @Override
    public void broadcast(IMessage message) {
        for (int i = 0; i < sources.size(); i++) {
            try{
                sources.get(i).send(message);
            }
            catch (Exception error){
                System.out.println(error.getMessage());
            }
        }
    }

    @Override
    public void sendMessage(ISource source, IMessage message) {
        source.send(message);
    }

    @Override
    public void handler(IMessageHandler messageHandler) {
        handlers.add(messageHandler);
    }


    public void addSource(ISource source){
        sources.add(source);
    }

}
