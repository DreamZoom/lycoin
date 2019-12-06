package com.ying.cloud.lycoin.event;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class EventExecutor implements IEventExecutor {

    private List<IEventListener> listeners;
    private Timer timer;
    public EventExecutor(){
        listeners =new ArrayList<>();
        timer=new Timer();
    }

    @Override
    public  void addEventListener(IEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeEventListener(IEventListener listener) {
        listeners.remove(listener);
    }

    @Override
    public synchronized void dispatch(IEvent event) {
        for (int i = 0; i < listeners.size(); i++) {
            IEventListener listener = this.listeners.get(i);
            ParameterizedType type = (ParameterizedType)(listener.getClass().getGenericInterfaces()[0]);
            type = (ParameterizedType)type.getActualTypeArguments()[0];

            if(event.getTypeClass().equals(type.getActualTypeArguments()[0])){
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        listener.handle(event);
                    }
                },0);
            }
        }
    }
}
