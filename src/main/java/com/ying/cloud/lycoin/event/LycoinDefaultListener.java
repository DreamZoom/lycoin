package com.ying.cloud.lycoin.event;

public class LycoinDefaultListener<TEvent extends LycoinEvent> implements  LycoinEventListener<TEvent> {

    @Override
    public boolean accept(LycoinEvent tEvent) {
        return true;
    }

    @Override
    public void action(TEvent event) {

    }
}
