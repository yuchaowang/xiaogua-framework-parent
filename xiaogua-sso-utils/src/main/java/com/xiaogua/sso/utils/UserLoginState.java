package com.xiaogua.sso.utils;

/**
 * 用户登录态
 *
 * @author: wangyc
 * @date: 2020-11-12
 **/
public class UserLoginState {
    /**
     * 用户UU号
     */
    private int uu;
    /**
     * 企业UU号，为0则说明，非企业登录
     */
    private int enuu;

    /**
     * 用户UU号
     *
     * @return
     */
    public int getUu() {
        return uu;
    }

    /**
     * 用户UU号
     *
     * @param uu
     */
    public void setUu(int uu) {
        this.uu = uu;
    }

    /**
     * 企业UU号，为0则说明，非企业登录
     *
     * @return
     */
    public int getEnuu() {
        return enuu;
    }

    /**
     * 企业UU号，为0则说明，非企业登录
     *
     * @param enuu
     */
    public void setEnuu(int enuu) {
        this.enuu = enuu;
    }
}
