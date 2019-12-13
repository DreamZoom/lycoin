package com.ying.cloud.lycoin.net;


import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public abstract class PeerNode<TSource extends Source> implements IPeerNode<TSource> {

    protected SourceCollection<TSource> sources ;
    

    public PeerNode(){
        sources=new SourceCollection<>();
    }
    public void setHandler(IMessageHandler<TSource> handler) {
        this.handler = handler;
    }

    protected IMessageHandler<TSource> handler;



    @Override
    public void setup() {

    }

    @Override
    public void broadcast(Message message){
        for (int i = 0; i <sources.size() ; i++) {
            send(sources.get(i),message);
        }
    }

    @Override
    public abstract void send(TSource source, Message message);

    @Override
    public void addSource(TSource source) {
        System.out.println("add source = "+source);
        Source source1 = getSource(source.id());
        if(source1!=null) return;
        sources.add(source);
    }



    @Override
    public void removeSource(TSource source) {
        sources.removeIf((s)->{
            return s.id().equals(source.id());
        });
    }

    @Override
    public TSource getSource(String id) {
        Iterator<TSource> iterator =  sources.iterator();
        while (iterator.hasNext()){
            TSource s = iterator.next();
            if(s==null) continue;
            if(s.id().equals(id)){
                return s;
            }
        }
        return null;
    }

    @Override
    public List<TSource> getSources() {
        return sources;
    }


}
