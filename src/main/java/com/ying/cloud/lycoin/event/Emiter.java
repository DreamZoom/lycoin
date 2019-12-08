package com.ying.cloud.lycoin.event;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class Emiter {
    private HashMap<Class, List<IEventListener>> listeners;
    private Timer timer;
    public Emiter(){
        listeners =new HashMap<>();
        timer=new Timer();
    }

    public <T> void addEventListener(Class<T> cls,IEventListener<T> listener){
        if(cls==null){
            return;
        }

        if(listener==null) return;

        if(listeners.containsKey(cls)){
            listeners.get(cls).add(listener);
        }
        else{
            listeners.put(cls,new ArrayList<>());
            listeners.get(cls).add(listener);
        }
    }

    public synchronized void dispatch(Object source, Object event) {
        if(event==null) return;
        Class cls = event.getClass();

        if(listeners.containsKey(cls)){
            for (int i = 0; i <listeners.get(cls).size() ; i++) {
                timer.schedule(new EmiterTask(i) {
                    @Override
                    public void run() {
                        listeners.get(cls).get((int)data).handle(source,event);
                    }
                },0);

            }
        }
    }
}
