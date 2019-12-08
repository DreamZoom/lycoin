package com.ying.cloud.lycoin.net;


public abstract class PeerNode<TSource extends Source> implements IPeerNode<TSource> {

    protected SourceCollection<TSource> sources ;

    public PeerNode(){
        sources=new SourceCollection<>();
    }
    public void setHandler(IMessageHandler handler) {
        this.handler = handler;
    }

    protected IMessageHandler handler;



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
        sources.add(source);
    }

    @Override
    public void removeSource(TSource source) {
        sources.removeIf((s)->{
            return s.id().equals(source);
        });
    }


}
