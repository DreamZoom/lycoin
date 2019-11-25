package com.ying.cloud.lycoin.event;


import java.util.*;

public class LycoinEventManager {
    private Collection<LycoinEventListener> listeners;
    public  LycoinEventManager(){
        listeners =Collections.synchronizedSet(new HashSet<>());
    }

    public synchronized void addEventListener(LycoinEventListener listener) {
        listeners.add(listener);
    }

    public synchronized void removeEventListener(LycoinEventListener listener) {
        listeners.remove(listener);
    }

    public synchronized void fireEvent(String eventName,Object ...data){
        LycoinEvent event =new LycoinEvent(this,eventName,data);
        Iterator iterator = listeners.iterator();
        while (iterator.hasNext()) {
            LycoinEventListener listener = (LycoinEventListener) iterator.next();

            if(accept(listener,event)){
                listener.action(event);
            }
        }
    }

    private boolean accept(LycoinEventListener listener,LycoinEvent event) {
        if(listener.name().equals(event.getEventName())){
            return true;
        }
        return  false;
    }


}
