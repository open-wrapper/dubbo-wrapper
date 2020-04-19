package org.apache.dubbo.rpc.protocol.http.converter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import com.googlecode.protobuf.format.JsonFormat;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.protocol.http.MethodParameterParser;

public class PbObjectConvert {

    private static final MethodParameterParser PARSER = MethodParameterParser.getInstance(); //singleton

    public static String convertToJsonString(ObjectMapper objectMapper, Object source) throws JsonProcessingException {
        if (GeneratedMessage.class.isAssignableFrom(source.getClass())) {
            return JsonFormat.printToString((Message) source);
        } else {
            return objectMapper.writeValueAsString(source);
        }
    }

    public static JsonNode convertToJsonNode(ObjectMapper objectMapper, Object source) throws JsonProcessingException, IOException {
        if (GeneratedMessage.class.isAssignableFrom(source.getClass())) {
            source = JsonFormat.printToString((Message) source);
            return objectMapper.readTree((String) source);
        } else {
            return objectMapper.valueToTree(source);
        }
    }

    public static Object convertToObject(ObjectMapper objectMapper, Class<?> targetClass, Object source)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, JsonParseException, JsonMappingException, IOException {
        if (GeneratedMessage.class.isAssignableFrom(targetClass)) {
            if (source instanceof JsonNode) {
                source = objectMapper.writeValueAsString(source);
            }
            Method m = targetClass.getMethod("newBuilder", null);
            Builder builder = (Builder) m.invoke(null, null);
            JsonFormat.merge((String) source, builder);
            return builder.build();
        } else if (source instanceof JsonNode) {
            return objectMapper.convertValue(source, targetClass);
        } else {
            return objectMapper.readValue((String) source, targetClass);
        }
    }

    /**
     * json转换成对象
     * @param objectMapper
     * @param method
     * @param jsonString
     * @return
     */
    public static Object[] convertToObjects(ObjectMapper objectMapper, Method method, String jsonString) throws IOException {
        Type[] types = method.getGenericParameterTypes();
        int length = types.length;
        Object[] result = new Object[length];

        if (StringUtils.isEmpty(jsonString) || length == 0) {
            return result;
        }

        String[] parameterNames = PARSER.parseParameterName(method);
        if (length == 1 && parameterNames.length == 0) {
            result[0] = objectMapper.readValue(jsonString, objectMapper.constructType(types[0]));
        } else {
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            for (int i = 0; i < length; i++) {
                JsonNode pNode = jsonNode.get(parameterNames[i]);
                if (pNode != null) {
                    result[i] = objectMapper.convertValue(pNode, objectMapper.constructType(types[i]));
                } else {
                    result[i] = null;
                }
            }
        }
        return result;
    }

}
