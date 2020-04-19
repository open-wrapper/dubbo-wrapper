package com.alibaba.dubbo.rpc.protocol.http.exception;


/**
 * http rpc 服务端调用业务逻辑过程中遇到异常，应当抛出此异常。
 * Created by zman on 16/7/21.
 */
public class HttpBusinessException extends org.apache.dubbo.rpc.protocol.http.exception.HttpBusinessException {


    public HttpBusinessException(int statusCode, String statusReason) {
        super(statusCode, statusReason);
    }

    public HttpBusinessException(int statusCode, String statusReason, Throwable e) {
        super(statusCode, statusReason, e);
    }

}
