package com.ying.cloud.lycoin.net;

public interface IConnectAdapter<TSource extends Source> {

    void onActive(TSource source);
    void onInactive(TSource source);
}
