package com.xiaogua.sso.utils;

import com.usoft.cat.utils.CatClientInterceptor;
import com.usoft.cat.utils.GRPCServerHelper;
import com.usoft.sso2.grpc.api.protobuf.ILoginGrpcServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * sso2 GRPC Client 连接配置
 *
 * @author: wangyc
 * @date: 2020-11-12
 */
@Configuration
public class Sso2GrpcConfig {

    @Value("${sso.grpc-host}") private String sso2GrpcHost;
    @Value("${sso.grpc-port}") private int sso2GrpcPort;
    @Value("${sso.grpc-timeout}") private int grpcTimeout;

    @Bean(name = "sso2ManagedChannel")
    public ManagedChannel getSso2ManagedChannel() {
        return ManagedChannelBuilder.forAddress(sso2GrpcHost, sso2GrpcPort).usePlaintext().build();
    }

    @Bean(name = "sso2LoginSBS")
    public ILoginGrpcServiceGrpc.ILoginGrpcServiceBlockingStub getLoginGrpcServiceBlockingStub(
        ManagedChannel sso2ManagedChannel) {
        ILoginGrpcServiceGrpc.ILoginGrpcServiceBlockingStub loginGrpcServiceBlockingStub =
            ILoginGrpcServiceGrpc.newBlockingStub(sso2ManagedChannel)
                .withOption(GRPCServerHelper.CO_KEY_CONNECTION_TIMEOUT, grpcTimeout)
                .withInterceptors(new CatClientInterceptor());
        return loginGrpcServiceBlockingStub;
    }

}
