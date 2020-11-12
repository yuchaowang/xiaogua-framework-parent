package com.xiaogua.sso.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * SSO 配置
 *
 * @author: wangyc
 * @date: 2020-11-12
 **/
@Component
@ConfigurationProperties("sso")
public class SsoProperties {
    /**
     * sso grpc host
     */
    private String grpcHost = "";
    /**
     * sso grpc port
     */
    private int grpcPort = 0;
    /**
     * sso grpc timeout
     */
    private int grpcTimeout = 0;

    /**
     * sso grpc host
     *
     * @return
     */
    public String getGrpcHost() {
        return grpcHost;
    }

    /**
     * sso grpc host
     *
     * @param grpcHost
     */
    public void setGrpcHost(String grpcHost) {
        this.grpcHost = grpcHost;
    }

    /**
     * sso grpc port
     *
     * @return
     */
    public int getGrpcPort() {
        return grpcPort;
    }

    /**
     * sso grpc port
     *
     * @param grpcPort
     */
    public void setGrpcPort(int grpcPort) {
        this.grpcPort = grpcPort;
    }

    /**
     * sso grpc timeout
     *
     * @return
     */
    public int getGrpcTimeout() {
        return grpcTimeout;
    }

    /**
     * sso grpc timeout
     *
     * @param grpcTimeout
     */
    public void setGrpcTimeout(int grpcTimeout) {
        this.grpcTimeout = grpcTimeout;
    }
}
