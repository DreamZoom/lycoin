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


    public synchronized void fireEvent(LycoinEvent event){
        Iterator iterator = listeners.iterator();
        while (iterator.hasNext()) {
            LycoinEventListener listener = (LycoinEventListener) iterator.next();
            if(listener.accept(event)){
                listener.action(event);
            }
        }
    }


}
