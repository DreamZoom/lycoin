package com.ying.cloud.lycoin.event;

import java.util.TimerTask;

public abstract class EmiterTask extends TimerTask {
    public EmiterTask(Object data) {
        this.data = data;
    }

    protected Object data;
}
