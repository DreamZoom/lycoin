package com.ying.cloud.lycoin;

import com.ying.cloud.lycoin.miner.Miner;
import com.ying.cloud.lycoin.models.Block;
import com.ying.cloud.lycoin.transaction.AuthorizationInfo;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/")
public class RestServer {

    public RestServer(Miner miner) {
        this.miner = miner;
    }

    Miner miner;

    @Path("/blocks") // 大括号里的是参数名，在函数位置使用@PathParam注解映射
    @GET // 声明这个接口必须GET访问
    @Produces(MediaType.APPLICATION_JSON) // 声明这个接口将以json格式返回
    public List<Block> getBlocks() {
        return miner.getChain().getBlocks();
    }

    @Path("/transaction") // 大括号里的是参数名，在函数位置使用@PathParam注解映射
    @POST // 声明这个接口必须GET访问
    @Produces(MediaType.APPLICATION_JSON) // 声明这个接口将以json格式返回
    public String transaction(AuthorizationInfo info){
        return "ok";
    }
}
