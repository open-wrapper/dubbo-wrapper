package org.apache.dubbo.rpc.protocol.http.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HttpJsonRequest {

    private Object businessParam;

    private Map<String, ?> publicParam;

    public Object getBusinessParam() {
        return businessParam;
    }

    public void setBusinessParam(Object businessParam) {
        this.businessParam = businessParam;
    }

    public Map<String, ?> getPublicParam() {
        return publicParam;
    }

    public void setPublicParam(Map<String, ?> publicParam) {
        this.publicParam = publicParam;
    }

    public String convertToParameter(ObjectMapper objectMapper) {
        StringBuilder sb = new StringBuilder();
        try {
            String paramString = PbObjectConvert.convertToJsonString(objectMapper, businessParam);
            paramString = URLEncoder.encode(paramString, "UTF-8");
            String publicString = URLEncoder.encode(objectMapper.writeValueAsString(publicParam), "UTF-8");
            sb.append("param=").append(paramString).append("&").append("public=").append(publicString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("转化HttpRequest遇到错误:" + businessParam + ":" + publicParam);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("进行utf-8编码错误:" + businessParam + ":" + publicParam);
        }
        return sb.toString();
    }

    /**
     * 对象数组转成JSON
     *
     * @param objectMapper
     * @param parameterNames
     * @return
     */
    public String convertToParameter(ObjectMapper objectMapper, String[] parameterNames) {
        StringBuilder sb = new StringBuilder();
        String paramString = "null";
        try {
            if (businessParam != null) {
                //businessParam为数组,说明方法有多个参数
                Object[] arr = (Object[]) businessParam;
                int length = arr.length;
                Map<String, Object> map = new HashMap<String, Object>(4);
                //一个参数且参数上没有注解(兼容旧版本)
                if (length == 1 && parameterNames.length == 0) {
                    paramString = objectMapper.writeValueAsString(arr[0]);
                } else {
                    for (int i = 0; i < length; i++) {
                        map.put(parameterNames[i], arr[i]);
                    }
                    paramString = objectMapper.writeValueAsString(map);
                }
            }
            paramString = URLEncoder.encode(paramString, "UTF-8");
            String publicString = URLEncoder.encode(objectMapper.writeValueAsString(publicParam), "UTF-8");
            sb.append("param=").append(paramString).append("&").append("public=").append(publicString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("转化HttpRequest遇到错误:" + businessParam + ":" + publicParam);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("进行utf-8编码错误:" + businessParam + ":" + publicParam);
        }
        return sb.toString();
    }
}
