package org.apache.dubbo.rpc.protocol.http.converter;

import com.alibaba.dubbo.rpc.protocol.http.converter.Status;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


/**
 * http返回值封装对象
 */
@JsonSerialize(using = HttpJsonResponseSerializer.class)
public class HttpJsonResponse {

    private Status status;

    private JsonNode result;

    public HttpJsonResponse(){
        status = new Status();
    }

    public HttpJsonResponse(JsonNode result) {
        this();
        this.result = result;
    }

    public void setStatusCode(int statusCode) {
        status.setCode(statusCode);
    }

    public void setStatusReason(String statusReason) {
        status.setReason(statusReason);
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

    @Override
    public String toString() {
        return "HttpJsonResponse [status=" + status + ", result=" + result + "]";
    }



}
