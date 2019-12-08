package com.ying.cloud.lycoin;


/**
 * Lycoin 币应用容器
 */
public class LycoinApplication extends BlockApplication {


    @Override
    public void init(LycoinContext context) {
//        this.handler(new IMessageHandler<MessageBlock>() {
//            @Override
//            public void handle( ISource source, MessageBlock message) {
//                Block block = message.getBlock();
//                if(context.getChain().accept(block)){
//
//                    List<TransactionCoin> list = block.getBody().map((iTransaction)->{
//                        return (TransactionCoin)iTransaction;
//                    });
//
//                    context.getTransactions().updateUnspentTransactionOut(list);
//
//                    System.out.println(message.toString());
//                    message.setTag("broadcast");
//                    context.getNetwork().broadcast(message);
//                }
//
//            }
//        });
//
//        /**
//         * 请求块消息处理
//         */
//        this.handler(new IMessageHandler<MessageRequestBlock>() {
//            @Override
//            public void handle(ISource source, MessageRequestBlock message) {
//                System.out.println(message.toString());
//                String hash = message.getHash();
//                Block block = context.getChain().findBlock(hash);
//                if(block!=null){
//                    MessageBlock messageBlock =new MessageBlock("broadcast",block);
//                    context.getNetwork().broadcast(messageBlock);
//                }
//            }
//        });
//
//        /**
//         * 交易消息处理
//         *
//         */
//        this.handler(new IMessageHandler<MessageTransaction>() {
//            @Override
//            public void handle(ISource source, MessageTransaction message) {
//                Transaction transaction = message.getTransaction();
//                if(context.getTransactions().addTransaction(transaction)) {
//                    context.getNetwork().broadcast(message);
//                }
//            }
//        });
    }

    @Override
    public void run() {
//        context.getChain().findNextBlock(context,( block)->{
//            MessageBlock messageBlock =new MessageBlock("find",block);
//            context.getNetwork().trigger(messageBlock,null);
//            return  true;
//        });
    }
}
