package com.xiaogua.sso.utils;

import com.usoft.sso2.grpc.api.protobuf.CheckLoginRequest;
import com.usoft.sso2.grpc.api.protobuf.CheckLoginResponse;
import com.usoft.sso2.grpc.api.protobuf.ILoginGrpcServiceGrpc;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Sso 控制层 基类
 *
 * @author: wangyc
 * @date: 2020-11-12
 **/
public abstract class BaseSsoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseSsoController.class);
    /**
     * 默认异常号 100
     */
    private static final int ERROR_CODE_100 = 100;
    /**
     * 身份验证token
     */
    private static final String TOKEN_COOKIE_NAME = "token";
    @Autowired private SsoProperties ssoProperties;
    @Autowired private ILoginGrpcServiceGrpc.ILoginGrpcServiceBlockingStub sso2LoginSBS;

    @PostConstruct
    public void init() {
        if (StringUtils.isBlank(ssoProperties.getGrpcHost())) {
            throw new RuntimeException("SSO Grpc Host不能为空，请在Properties文件配置sso.grpc-host");
        }
        if (ssoProperties.getGrpcPort() <= 0) {
            throw new RuntimeException("SSO Grpc Port必需大于0，请在Properties文件配置sso.grpc-port");
        }
        if (ssoProperties.getGrpcTimeout() <= 0) {
            throw new RuntimeException("SSO Grpc Timeout必需大于0，请在Properties文件配置sso.grpc-timeout");
        }
    }

    /**
     * 获取登录态
     *
     * @param request
     * @return
     */
    protected UserLoginState getUserLoginState(HttpServletRequest request) {
        String token = getCookie(request, TOKEN_COOKIE_NAME);
        if (StringUtils.isBlank(token)) {
            return null;
        }
        com.usoft.sso2.grpc.api.entity.UserLoginState state = checkLogin(token);
        if (state == null) {
            return null;
        }
        UserLoginState userLoginState = new UserLoginState();
        userLoginState.setUu(state.getUu());
        userLoginState.setEnuu(state.getEnuu());
        return userLoginState;
    }

    /**
     * 验证登录态
     *
     * @param token
     * @return
     */
    private com.usoft.sso2.grpc.api.entity.UserLoginState checkLogin(String token) {
        try {
            //此处使用异常捕获，原因：如果账户服务宕机，避免严重影响各应用服务
            CheckLoginRequest.Builder clRequest = CheckLoginRequest.newBuilder();
            clRequest.setToken(token);
            CheckLoginResponse clResponse = sso2LoginSBS.checkLogin(clRequest.build());
            if (clResponse.getResponseHeader().getCode() != 0) {
                if (clResponse.getResponseHeader().getCode() == ERROR_CODE_100) {
                    //系统异常抛出异常
                    throw new RuntimeException(clResponse.getResponseHeader().getMsg());
                }
                return null;
            }
            if (clResponse.getUserLoginState() == null) {
                return null;
            }
            if (clResponse.getUserLoginState()
                .equals(com.usoft.sso2.grpc.api.entity.UserLoginState.getDefaultInstance())) {
                //与默认由相等，则相当于Null
                return null;
            }
            return clResponse.getUserLoginState();
        } catch (Exception e) {
            LOGGER.error("验证登录态[BaseSsoController.checkLogin].异常：", e);
        }
        return null;
    }

    /**
     * 获取Cookie值
     *
     * @param request
     * @param cookieName
     * @return
     */
    private String getCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        String cookieVale = "";
        if (ArrayUtils.isNotEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    cookieVale = cookie.getValue();
                    break;
                }
            }
        }
        return cookieVale;
    }
}
