package org.apache.dubbo.rpc.protocol.http.exception;

import com.alibaba.dubbo.rpc.RpcException;

/**
 * http rpc 服务端调用业务逻辑过程中遇到异常，应当抛出此异常。
 */
public class HttpBusinessException extends RpcException {

    private int statusCode;

    private String statusReason;

    public HttpBusinessException(int statusCode, String statusReason) {
        this.statusCode = statusCode;
        this.statusReason = statusReason;
    }

    public HttpBusinessException(int statusCode, String statusReason, Throwable e) {
        super(statusReason, e);
        this.statusCode = statusCode;
        this.statusReason = statusReason;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }
}
