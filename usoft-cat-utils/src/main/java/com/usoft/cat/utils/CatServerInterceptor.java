/**
 *
 */
package com.usoft.cat.utils;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.message.spi.MessageTree;
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cat Server 拦截器
 *
 * @author wangcanyi
 */
public class CatServerInterceptor implements ServerInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(CatServerInterceptor.class);
    /**
     * GRPC 服务端口
     */
    private final String grpcServerPort;

    public CatServerInterceptor(String grpcServerPort) {
        this.grpcServerPort = grpcServerPort;
    }

    @Override
    public <ReqT, RespT> Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> call, final Metadata headers, ServerCallHandler<ReqT, RespT> next) {

        return new SimpleForwardingServerCallListener<ReqT>(next.startCall(new SimpleForwardingServerCall<ReqT, RespT>(call) {
            @Override
            public void sendHeaders(Metadata responseHeaders) {
                String serviceIP = IPUtil.getLocalHostAddress();
                String servicePort = grpcServerPort;
                responseHeaders.put(GRPCServerHelper.MD_KEY_SERVICE_IP_KEY, serviceIP);
                responseHeaders.put(GRPCServerHelper.MD_KEY_SERVICE_PORT_KEY, servicePort);
                super.sendHeaders(responseHeaders);
            }
        }, headers)) {
            @Override
            public void onHalfClose() {
                Transaction t = Cat.newTransaction("PigeonService", call.getMethodDescriptor().getFullMethodName());
                String clientIP = "Unknown";
                if (headers.containsKey(GRPCServerHelper.MD_KEY_CLIENT_IP)) {
                    clientIP = headers.get(GRPCServerHelper.MD_KEY_CLIENT_IP);
                }
                t.addData("ClientIp", clientIP);
                t.addData("ServerIp", IPUtil.getLocalHostAddress());
                Cat.logEvent("PigeonService.client", clientIP);
                // 设置cross信息
                String appName = "Unknown";
                if (headers.containsKey(GRPCServerHelper.MD_KEY_CLIENT_APP_NAME)) {
                    appName = headers.get(GRPCServerHelper.MD_KEY_CLIENT_APP_NAME);
                }
                Cat.logEvent("PigeonService.app", appName);
                // 设置logview信息
                MessageTree tree = Cat.getManager().getThreadLocalMessageTree();
                if (headers.containsKey(GRPCServerHelper.MD_KEY_CHILD_ID)) {
                    tree.setMessageId(headers.get(GRPCServerHelper.MD_KEY_CHILD_ID));
                }
                if (headers.containsKey(GRPCServerHelper.MD_KEY_ROOT_MESSAGE_ID)) {
                    tree.setRootMessageId(headers.get(GRPCServerHelper.MD_KEY_ROOT_MESSAGE_ID));
                }
                if (headers.containsKey(GRPCServerHelper.MD_KEY_MESSAGE_ID)) {
                    tree.setParentMessageId(headers.get(GRPCServerHelper.MD_KEY_MESSAGE_ID));
                }

                try {
                    this.delegate().onHalfClose();
                    t.setStatus(Transaction.SUCCESS);
                } catch (Exception e) {
                    t.setStatus(e);
                    //日志记录重复
                    //LOG.error("GRPCServer.CatServerInterceptor.onHalfClose.异常:", e);
                    throw e;
                } finally {
                    t.complete();
                }
            }

            @Override
            public void onComplete() {
                Transaction t = Cat.newTransaction("GRPC", "GRPCServer.CatServerInterceptor.onComplete");
                t.addData("FullMethodName", call.getMethodDescriptor().getFullMethodName());
                super.onComplete();
                t.setStatus(Transaction.SUCCESS);
                t.complete();
            }

            @Override
            public void onMessage(ReqT message) {
                Transaction t = Cat.newTransaction("GRPC", "GRPCServer.CatServerInterceptor.onMessage");
                t.addData("FullMethodName", call.getMethodDescriptor().getFullMethodName());
                super.onMessage(message);
                t.setStatus(Transaction.SUCCESS);
                t.complete();
            }

            @Override
            public void onReady() {
                Transaction t = Cat.newTransaction("GRPC", "GRPCServer.CatServerInterceptor.onReady");
                t.addData("FullMethodName", call.getMethodDescriptor().getFullMethodName());
                super.onReady();
                t.setStatus(Transaction.SUCCESS);
                t.complete();
            }

            @Override
            public void onCancel() {// 调用被取消时
                Transaction t = Cat.newTransaction("GRPC", "GRPCServer.CatServerInterceptor.onCancel");
                t.addData("FullMethodName", call.getMethodDescriptor().getFullMethodName());
                super.onCancel();
                t.setStatus(Transaction.SUCCESS);
                t.complete();
            }
        };
    }
}
