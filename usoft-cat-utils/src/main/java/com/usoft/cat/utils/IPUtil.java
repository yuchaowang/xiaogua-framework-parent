/**
 *
 */
package com.usoft.cat.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;


/**
 * IP服务器相关辅助类
 *
 * @author wangcanyi
 */
public class IPUtil {
    private static final Logger LOG = LoggerFactory.getLogger(IPUtil.class);

    private IPUtil() {

    }

    /**
     * 获取本机机器名
     *
     * @return
     */
    public static String getLocalHostName() {
        String hostName = "";
        InetAddress localHost = IPUtil.getLocalHost();
        if (localHost != null) {
            hostName = localHost.getHostName();
        }
        return hostName;
    }

    /**
     * 获取本机IP地址
     *
     * @return
     */
    public static String getLocalHostAddress() {
        String localip = null;// 本地IP，如果没有配置外网IP则返回它
        String netip = null;// 外网IP
        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            boolean finded = false;// 是否找到外网IP
            while (netInterfaces.hasMoreElements() && !finded) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> address = ni.getInetAddresses();
                while (address.hasMoreElements()) {
                    ip = address.nextElement();
                    if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 外网IP
                        netip = ip.getHostAddress();
                        finded = true;
                        break;
                    } else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 内网IP
                        localip = ip.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("获取本机IP地址[IPUtil.getLocalHostAddress].异常", e);
        }

        if (netip != null && !"".equals(netip)) {
            return netip;
        }
        return localip;
    }

    /**
     * 获取本机机器信息
     *
     * @return
     */
    private static InetAddress getLocalHost() {
        InetAddress localHost = null;
        try {
            localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            LOG.error("获取本机机器信息[IPUtil.getLocalHost].异常", e);
        }
        return localHost;
    }
}
