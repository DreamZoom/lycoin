package com.ying.cloud.lycoin.net;

import com.ying.cloud.lycoin.event.Event;
import com.ying.cloud.lycoin.event.EventSource;
import com.ying.cloud.lycoin.event.GlobalEventExecutor;
import com.ying.cloud.lycoin.models.Message;
import com.ying.cloud.lycoin.net.events.MessageEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class Network implements INetwork {
    List<Source> sources;

    public Network(){
        this.sources =new ArrayList<>();
    }


    @Override
    public void setup() {

    }

    @Override
    public void broadcast(Message message) {
        for (int i = 0; i < sources.size(); i++) {
            try{
                sendMessage(sources.get(i),message);
            }
            catch (Exception error){
                System.out.println(error.getMessage());
            }
        }
    }

    @Override
    public void sendMessage(Source source, Message message) {

    }


    @Override
    public void receiveMessage(Source source, Message message) {
        EventSource eventSource =new EventSource(source);
        GlobalEventExecutor.INSTANCE.dispatch(new Event<>(eventSource,message));
    }


    public void addSource(Source source){
        sources.add(source);
    }

    @Override
    public void removeSource(Source source) {
        sources.remove(source);
    }

}
