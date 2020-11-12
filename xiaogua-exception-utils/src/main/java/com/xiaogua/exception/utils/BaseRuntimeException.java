package com.xiaogua.exception.utils;

/**
 * Base公用异常
 *
 * @author: wangyc
 * @date: 2020-11-12
 */
public abstract class BaseRuntimeException extends RuntimeException {
    /**
     * 默认异常号 100
     */
    public static final int ERROR_CODE_100 = 100;
    /**
     * 服务器异常,请重试
     */
    public static final String ERROR_CODE_100_MSG = "服务器异常,请重试";

    private int errorCode;
    private String errorMessage;

    /**
     * Base公用异常
     *
     * @param errorMessage 错误信息
     */
    public BaseRuntimeException(String errorMessage) {
        this(ERROR_CODE_100, errorMessage);
    }

    /**
     * Base公用异常
     *
     * @param errorCode    错误号
     * @param errorMessage 错误信息
     */
    public BaseRuntimeException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * 错误号
     *
     * @return
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode 错误号
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * 错误信息
     *
     * @return
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage 错误信息
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
