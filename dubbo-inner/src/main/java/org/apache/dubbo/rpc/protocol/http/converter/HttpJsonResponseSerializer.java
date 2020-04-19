package org.apache.dubbo.rpc.protocol.http.converter;

import com.alibaba.dubbo.rpc.protocol.http.HttpJsonHandler;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

/**
 * 自定义输出业务层的response
 */
public class HttpJsonResponseSerializer extends StdSerializer<HttpJsonResponse> {
    public HttpJsonResponseSerializer() {
        this(null);
    }

    public HttpJsonResponseSerializer(Class<HttpJsonResponse> t) {
        super(t);
    }

    @Override
    public void serialize(HttpJsonResponse result, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        //如果业务要求自定义输出json，则输出HttpJsonResponse里面的result对象（这是业务层定义的response)
        Boolean flag = HttpJsonHandler.getUserDefinedResponseFlag();
        if (flag != null && flag.booleanValue() == true) {
            jsonGenerator.writeObject(result.getResult());
        }else {
            HttpJsonResponseClone res = new HttpJsonResponseClone();
            res.setStatus(result.getStatus());
            res.setResult(result.getResult());
            jsonGenerator.writeObject(res); //如果直接操作result将产生循环
        }
    }
}
