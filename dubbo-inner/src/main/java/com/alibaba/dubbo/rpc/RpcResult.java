package com.alibaba.dubbo.rpc;

import java.util.function.BiConsumer;
import org.apache.dubbo.rpc.AsyncRpcResult;
import org.apache.dubbo.rpc.InvokeMode;
import org.apache.dubbo.rpc.RpcInvocation;

/**
 * @author codel
 * @since 2020-01-14
 */
@Deprecated
public class RpcResult extends Result.CompatibleResult {

    public RpcResult(Object result) {
        super(buildResult(result));
    }

    public RpcResult(Throwable exception) {
        super(buildResult(exception));
    }

    public RpcResult(org.apache.dubbo.rpc.Result result) {
        super(result);
    }

    public static org.apache.dubbo.rpc.Result buildResult(Object result) {
        RpcInvocation rpcInvocation = new RpcInvocation();
        rpcInvocation.setInvokeMode(InvokeMode.SYNC);
        return AsyncRpcResult.newDefaultAsyncResult(result, rpcInvocation);

    }

    public static org.apache.dubbo.rpc.Result buildResult(Throwable exception) {
        RpcInvocation rpcInvocation = new RpcInvocation();
        rpcInvocation.setInvokeMode(InvokeMode.SYNC);
        return AsyncRpcResult.newDefaultAsyncResult(exception, rpcInvocation);

    }


    @Override
    public org.apache.dubbo.rpc.Result whenCompleteWithContext(BiConsumer<org.apache.dubbo.rpc.Result, Throwable> fn) {
        return getDelegate().whenCompleteWithContext(fn);
    }
}
