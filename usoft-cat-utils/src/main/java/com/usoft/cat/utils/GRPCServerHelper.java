/**
 *
 */
package com.usoft.cat.utils;

import io.grpc.CallOptions;
import io.grpc.Metadata;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import org.apache.commons.lang3.StringUtils;

/**
 * GRPC 服务常量
 *
 * @author wangcanyi
 */
public class GRPCServerHelper {
    private GRPCServerHelper() {

    }

    /**
     * 客户端IP地址
     */
    public final static Metadata.Key<String> MD_KEY_CLIENT_IP = Metadata.Key.of("MDKey_ClientIP", Metadata.ASCII_STRING_MARSHALLER);
    /**
     * 客户端应用名
     */
    public final static Metadata.Key<String> MD_KEY_CLIENT_APP_NAME = Metadata.Key.of("MDKey_ClientAppName", Metadata.ASCII_STRING_MARSHALLER);
    /**
     * Cat Child 消息Id
     */
    public final static Metadata.Key<String> MD_KEY_CHILD_ID = Metadata.Key.of("MDKey_ChildId", Metadata.ASCII_STRING_MARSHALLER);
    /**
     * Cat 根 消息Id
     */
    public final static Metadata.Key<String> MD_KEY_ROOT_MESSAGE_ID = Metadata.Key.of("MDKey_RootMessageId", Metadata.ASCII_STRING_MARSHALLER);
    /**
     * Cat 消息Id
     */
    public final static Metadata.Key<String> MD_KEY_MESSAGE_ID = Metadata.Key.of("MDKey_MessageId", Metadata.ASCII_STRING_MARSHALLER);
    /**
     * 服务端IP地址
     */
    public final static Metadata.Key<String> MD_KEY_SERVICE_IP_KEY = Metadata.Key.of("MDKey_ServiceIPKey", Metadata.ASCII_STRING_MARSHALLER);
    /**
     * 服务端端口
     */
    public final static Metadata.Key<String> MD_KEY_SERVICE_PORT_KEY = Metadata.Key.of("MDKey_ServicePortKey", Metadata.ASCII_STRING_MARSHALLER);
    /**
     * 异常错误码
     */
    public final static Metadata.Key<String> MD_KEY_ERROR_CODE = Metadata.Key.of("MDKey_ErrorCode", Metadata.ASCII_STRING_MARSHALLER);
    /**
     * 异常错误信息
     */
    public final static Metadata.Key<byte[]> MD_KEY_ERROR_MSG = Metadata.Key.of("MDKey_ErrorMsg-bin", Metadata.BINARY_BYTE_MARSHALLER);
    /**
     * 是否异步接口
     */
    public final static CallOptions.Key<Boolean> CO_KEY_IS_ASYNC_INTERFACE = CallOptions.Key.of("COKey_IsAsyncInterface", false);
    /**
     * 接口连接超时时间,单位毫秒
     */
    public final static CallOptions.Key<Integer> CO_KEY_CONNECTION_TIMEOUT = CallOptions.Key.of("COKey_ConnectionTimeout", 0);

    /**
     * 获取异常信息Metadata
     *
     * @param e
     * @return
     */
    public static Metadata getMetadataFromException(Exception e) {
        Metadata metadata = new Metadata();
        if (e != null) {
            String code = "";
            String msg = "";
            if (StringUtils.isNotBlank(e.getMessage())) {
                msg = e.getMessage();
            }
            metadata.put(GRPCServerHelper.MD_KEY_ERROR_CODE, code);
            metadata.put(GRPCServerHelper.MD_KEY_ERROR_MSG, msg.getBytes());
        }
        return metadata;
    }

    /**
     * 获取异常信息 根据元数据
     *
     * @param metadata
     * @return
     */
    public static String getExceptionStringFromMetadata(Metadata metadata) {
        String exStr = "";
        if (metadata != null) {
            if (metadata.containsKey(MD_KEY_ERROR_CODE)) {
                exStr = String.format("%sErrorCode:%s;", exStr, metadata.get(MD_KEY_ERROR_CODE));
                //exStr += "ErrorCode:" + metadata.get(MD_KEY_ERROR_CODE) + ";";
            }
            if (metadata.containsKey(MD_KEY_ERROR_MSG)) {
                exStr = String.format("%ErrorMsg:%s;", exStr, metadata.get(MD_KEY_ERROR_MSG));
                //exStr += "ErrorMsg:" + new String(metadata.get(MD_KEY_ERROR_MSG)) + ";";
            }
        }
        return exStr;
    }

    /**
     * 获取异常信息 根据 GRPC异常
     *
     * @param e
     * @return
     */
    public static String getExceptionStringFromException(Exception e) {
        String exStr = "";
        if (e != null) {
            exStr = e.getMessage();
            if (e instanceof StatusException) {
                exStr = new StringBuffer().append(exStr).append(";").append(getExceptionStringFromMetadata(((StatusException) e).getTrailers())).toString();
                //exStr = exStr + ";" + getExceptionStringFromMetadata(((StatusException) e).getTrailers());
            } else if (e instanceof StatusRuntimeException) {
                exStr = new StringBuffer().append(exStr).append(";").append(getExceptionStringFromMetadata(((StatusRuntimeException) e).getTrailers())).toString();
                //exStr = exStr + ";" + getExceptionStringFromMetadata(((StatusRuntimeException) e).getTrailers());
            }
        }
        return exStr;
    }

    /**
     * 获取异常号 根据 GRPC异常
     *
     * @param e
     * @return
     */
    public static String getExceptionCodeFromException(Exception e) {
        String exCode = "";
        if (e != null) {
            Metadata metadata = null;
            if (e instanceof StatusException) {
                metadata = ((StatusException) e).getTrailers();
            } else if (e instanceof StatusRuntimeException) {
                metadata = ((StatusRuntimeException) e).getTrailers();
            }
            if (metadata != null) {
                exCode = metadata.get(MD_KEY_ERROR_CODE);
            }
        }
        return exCode;
    }

    /**
     * 获取异常消息 根据 GRPC异常
     *
     * @param e
     * @return
     */
    public static String getExceptionMsgFromException(Exception e) {
        String exMsg = "";
        if (e != null) {
            Metadata metadata = null;
            if (e instanceof StatusException) {
                metadata = ((StatusException) e).getTrailers();
            } else if (e instanceof StatusRuntimeException) {
                metadata = ((StatusRuntimeException) e).getTrailers();
            }
            if (metadata != null && metadata.containsKey(MD_KEY_ERROR_MSG)) {
                exMsg = new String(metadata.get(MD_KEY_ERROR_MSG));
            } else {
                exMsg = e.getMessage();
            }
        }
        return exMsg;
    }

   /* *//**
     * 验证两集合是否相等
     *
     * @param list1
     * @param list2
     * @return
     *//*
    public static boolean equalsList(List<?> list1, List<?> list2) {
        if (CollectionUtils.isEmpty(list1) && CollectionUtils.isEmpty(list2)) {
            return true;
        }
        if (list1 != null && list2 != null && list1.size() == list2.size() && list1.containsAll(list2) && list2.containsAll(list1)) {
            return true;
        }
        return false;
    }*/
}
