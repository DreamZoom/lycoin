package com.ying.cloud.lycoin.net;

public interface ISourceAdapter<TSource extends Source> {
    void onAdded(TSource source);
    void onRemoved(TSource source);
}
