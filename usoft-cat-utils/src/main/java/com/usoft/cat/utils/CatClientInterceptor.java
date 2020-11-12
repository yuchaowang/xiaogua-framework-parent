/**
 *
 */
package com.usoft.cat.utils;

import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.message.spi.MessageTree;
import io.grpc.*;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * CAT Client 拦截器
 *
 * @author wangcanyi
 */
public class CatClientInterceptor implements ClientInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(CatClientInterceptor.class);

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        CallOptions newCallOptions = callOptions;
        int connectionTimeout = newCallOptions.getOption(GRPCServerHelper.CO_KEY_CONNECTION_TIMEOUT);
        if (connectionTimeout > 0) {// 接口超时设置
            newCallOptions = newCallOptions.withDeadlineAfter(connectionTimeout, TimeUnit.MILLISECONDS);
        }
        String fullMethodName = method.getFullMethodName();
        String authority = next.authority();// 应用名
        final boolean isAsyncInterface = newCallOptions.getOption(GRPCServerHelper.CO_KEY_IS_ASYNC_INTERFACE);
        final Transaction t = Cat.newTransaction("PigeonCall", fullMethodName);
        Cat.logEvent("PigeonCall.app", authority);
        return new SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, newCallOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                if (headers != null) {
                    MessageTree tree = Cat.getManager().getThreadLocalMessageTree();
                    // 设置LogView消息Id
                    String childId = Cat.createMessageId();
                    Cat.logEvent(CatConstants.TYPE_REMOTE_CALL, "", Event.SUCCESS, childId);
                    String rootMessageId = tree.getRootMessageId();
                    String messageId = tree.getMessageId();
                    if (messageId == null) {
                        messageId = Cat.createMessageId();
                        tree.setMessageId(messageId);
                    }
                    if (rootMessageId == null) {
                        rootMessageId = messageId;
                    }
                    headers.put(GRPCServerHelper.MD_KEY_CLIENT_IP, IPUtil.getLocalHostAddress());
                    headers.put(GRPCServerHelper.MD_KEY_CLIENT_APP_NAME, Cat.getManager().getDomain());
                    headers.put(GRPCServerHelper.MD_KEY_CHILD_ID, childId);
                    if (rootMessageId != null) {
                        headers.put(GRPCServerHelper.MD_KEY_ROOT_MESSAGE_ID, rootMessageId);
                    }
                    if (messageId != null) {
                        headers.put(GRPCServerHelper.MD_KEY_MESSAGE_ID, messageId);
                    }
                }
                super.start(new SimpleForwardingClientCallListener<RespT>(responseListener) {
                    @Override
                    public void onHeaders(Metadata headers) {
                        String serviceIP = "";
                        String servicePort = "";
                        if (headers.containsKey(GRPCServerHelper.MD_KEY_SERVICE_IP_KEY)) {
                            serviceIP = headers.get(GRPCServerHelper.MD_KEY_SERVICE_IP_KEY);
                        }
                        if (headers.containsKey(GRPCServerHelper.MD_KEY_SERVICE_PORT_KEY)) {
                            servicePort = headers.get(GRPCServerHelper.MD_KEY_SERVICE_PORT_KEY);
                        }
                        Cat.logEvent("PigeonCall.server", serviceIP);
                        Cat.logEvent("PigeonCall.port", servicePort);
                        super.onHeaders(headers);
                    }

                    @Override
                    public void onClose(Status status, Metadata trailers) {
                        if (!isAsyncInterface) {// 同步接口时，才进行CAT记录
                            if (status.isOk()) {
                                t.setStatus(Transaction.SUCCESS);
                            } else {
                                StatusException se = status.asException(trailers);
                                t.setStatus(se);
                                //日志记录重复
                                //LOG.error("GRPCServer.CatClientInterceptor.onClose.异常:" + GRPCServerHelper.getExceptionStringFromMetadata(trailers), se);
                            }
                            t.complete();
                        }
                        super.onClose(status, trailers);
                    }
                }, headers);
            }

            @Override
            public void halfClose() {
                super.halfClose();
                if (isAsyncInterface) {// 当为异常方法时，无需等待返回值，则关关闭时，认为已经成功
                    t.setStatus(Transaction.SUCCESS);
                    t.complete();
                }
            }

            @Override
            public void cancel(String message, Throwable cause) {
                if (!t.isCompleted()) {
                    t.setStatus(cause);
                    t.complete();// 异常时导致关闭
                    LOG.error("GRPCServer.CatClientInterceptor.cancel.异常:" + message, cause);
                }
                super.cancel(message, cause);
            }
        };
    }
}
