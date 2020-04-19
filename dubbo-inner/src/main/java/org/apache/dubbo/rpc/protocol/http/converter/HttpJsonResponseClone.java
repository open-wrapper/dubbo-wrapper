package org.apache.dubbo.rpc.protocol.http.converter;

import com.alibaba.dubbo.rpc.protocol.http.converter.Status;
import com.fasterxml.jackson.databind.JsonNode;


/**
 * http返回值封装对象，是HttpJsonResponse的克隆版本，去掉了一些函数，仅供序列化输出时使用；
 * 通过这个类避免了HttpJsonResponse自定义序列化输出时的无限循环。
 * @date 20170519
 */
public class HttpJsonResponseClone {

    private Status status;

    private JsonNode result;

    public HttpJsonResponseClone(){
        status = new Status();
    }

    public HttpJsonResponseClone(JsonNode result) {
        this();
        this.result = result;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public JsonNode getResult() {
        return result;
    }

    public void setResult(JsonNode result) {
        this.result = result;
    }

}
