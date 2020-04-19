package com.alibaba.dubbo.rpc.protocol.http.converter;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Status {

    @JsonProperty("status_code")
    private int code;

    @JsonProperty("status_reason")
    private String reason;

    public Status() {
    }


    public int getCode() {
        return code;
    }


    public void setCode(int code) {
        this.code = code;
    }


    public String getReason() {
        return reason;
    }


    public void setReason(String reason) {
        this.reason = reason;
    }


    @Override
    public String toString() {
        return "Status [code=" + code + ", reason=" + reason + "]";
    }


}
