package com.ying.cloud.lycoin.net;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Function;

public class SourceCollection<TSource extends Source> extends ArrayList<TSource> {

    public Source findIf(Function<TSource,Boolean> filter){
        Iterator<TSource> iterator =  this.iterator();
        while (iterator.hasNext()){
            TSource source = iterator.next();
            if(filter.apply(source)){
                return source;
            }
        }
        return null;
    }

    public TSource findById(Source source){
        Iterator<TSource> iterator =  this.iterator();
        while (iterator.hasNext()){
            TSource s = iterator.next();
            if(s==null) continue;
            if(s.id().equals(source.id())){
                return s;
            }
        }
        return null;
    }
}
