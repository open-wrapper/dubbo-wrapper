package org.apache.dubbo.rpc.protocol.http;

import com.alibaba.dubbo.rpc.protocol.http.HttpJsonHandler;
import com.alibaba.dubbo.rpc.protocol.http.converter.Status;
import com.alibaba.dubbo.rpc.protocol.util.HttpUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.PathParam;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.utils.IOUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcInvocation;
import org.apache.dubbo.rpc.protocol.AbstractExporter;
import org.apache.dubbo.rpc.protocol.http.converter.HttpJsonResponse;
import org.apache.dubbo.rpc.protocol.http.converter.PbObjectConvert;
import org.apache.dubbo.rpc.protocol.http.exception.HttpBusinessException;

import static com.wrapper.dubbo.common.CommonConstants.FROM_IP;
import static com.wrapper.dubbo.common.CommonConstants.SPAN_ID;
import static com.wrapper.dubbo.common.CommonConstants.TRACE_ID;
import static com.wrapper.dubbo.common.CommonConstants.URL_PATH;
import static org.apache.dubbo.common.constants.CommonConstants.INTERFACE_KEY;

/**
 * 一个接口对应一个Exporter对象，此对象负责解析请求并分发到接口下相应的方法
 */
public class HttpJsonExporter<T> extends AbstractExporter<T> {

    private static final Logger logger = LoggerFactory.getLogger(HttpJsonExporter.class);

    private Map<String, Method> methodMapping;

    private String path;

    private ObjectMapper objectMapper = new ObjectMapper();

    public HttpJsonExporter(Invoker<T> invoker) {
        super(invoker);
        path = HttpUtils.fetchPath(invoker.getInterface());
        methodMapping = new ConcurrentHashMap<String, Method>();
        for (Method method : invoker.getInterface().getMethods()) {
            String subPath = HttpUtils.fetchSubpath(method);
            methodMapping.put(subPath, method);
            Class<?>[] types = method.getParameterTypes();
            if (types != null) {
                Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                int length = types.length;
                if (length == 1) {
                    //校验方法只有一个参数且参数类型为基本类型时参数有没有加@PathParam注解
                    for (Annotation[] annotations : parameterAnnotations) {
                        PathParam pathParam = null;
                        for (Annotation annotation : annotations) {
                            if (annotation instanceof PathParam) {
                                pathParam = (PathParam) annotation;
                            }
                        }
                        if (isBasicType(types[0])) {
                            if (pathParam == null) {
                                throw new RuntimeException(String.format("方法:%s,参数需要加上注解@PathParam.", method.getName()));
                            }
                            //检查注解上的值是否为空
                            String value = pathParam.value();
                            if (value == null || value.length() == 0) {
                                throw new RuntimeException(String.format("方法:%s,注解@PathParam上的value不能为空.", method.getName()));
                            }
                        }
                    }
                } else {
                    //校验方法有多个参数时参数有没有加@PathParam注解
                    for (Annotation[] annotations : parameterAnnotations) {
                        PathParam pathParam = null;
                        for (Annotation annotation : annotations) {
                            if (annotation instanceof PathParam) {
                                pathParam = (PathParam) annotation;
                            }
                        }
                        if (pathParam == null) {
                            throw new RuntimeException(String.format("方法:%s,参数需要加上注解@PathParam.", method.getName()));
                        }
                        //检查注解上的值是否为空
                        String value = pathParam.value();
                        if (value == null || value.length() == 0) {
                            throw new RuntimeException(String.format("方法:%s,注解@PathParam上的value不能为空.", method.getName()));
                        }
                    }
                }
            }
        }
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    }

    private boolean isBasicType(Class<?> clazz) {
        return
                (
                        clazz.equals(String.class) ||
                                clazz.equals(Integer.class) ||
                                clazz.equals(Byte.class) ||
                                clazz.equals(Long.class) ||
                                clazz.equals(Double.class) ||
                                clazz.equals(Float.class) ||
                                clazz.equals(Character.class) ||
                                clazz.equals(Short.class) ||
                                clazz.equals(BigDecimal.class) ||
                                clazz.equals(BigInteger.class) ||
                                clazz.equals(Boolean.class) ||
                                clazz.equals(Date.class) ||
                                clazz.isPrimitive()
                );

    }

    public void handleRequest(String methodName, HttpServletRequest request,
                              HttpServletResponse response) {
        try {
            HttpJsonHandler.clear();
            handleRequestImpl(methodName, request, response);
        } catch (IOException e) {
            logger.error("handle request exception. methodName:" + methodName + ",pathInfo:" + request.getPathInfo(), e);
        }
    }

    /**
     * 1. 从request提取参数到map
     * 2. 构建invocation对象
     * 3. 调用业务逻辑
     * 4. 转换处理结果为规定的json格式
     * 5. 输出response
     */
    public void handleRequestImpl(String methodName, HttpServletRequest request,
                                  HttpServletResponse response) throws IOException {
        HttpJsonResponse httpJsonResponse = new HttpJsonResponse();

        //提取参数
        Map<String, String> parameterMap = Collections.emptyMap();
        //构建invocation
        RpcInvocation invocation;
        try {
            parameterMap = extractParameters(request);
            invocation = constructRpcInvocation(methodName, request);
        } catch (Exception e) {
            logger.error("handle request impl exception. methodName:" + methodName + ",pathInfo:" + request.getPathInfo(), e);
            httpJsonResponse.setStatusCode(1);
            httpJsonResponse.setStatusReason("解析参数时遇到问题:" + e.getMessage());
            objectMapper.writeValue(response.getWriter(), httpJsonResponse);
            return;
        }
        HttpJsonHandler.setParameters(parameterMap);

        try {
            //调用invocation
            JsonNode resultJsonNode = invoke(invocation, methodName, parameterMap.get("param"));
            httpJsonResponse.setResult(resultJsonNode);
            //允许自定义status
            Status status = HttpJsonHandler.getStatus();
            if (status != null) {
                httpJsonResponse.setStatus(status);
            }

            PrintWriter writer = response.getWriter();

            String jsonpMethod = HttpJsonHandler.getJsonpMethod();
            if (jsonpMethod != null && !"".equals(jsonpMethod)) {
                writer.write(jsonpMethod + "(");
                writer.write(objectMapper.writeValueAsString(httpJsonResponse));
                writer.write(")");
            } else {
                objectMapper.writeValue(writer, httpJsonResponse);
            }

        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            String traceId = invocation.getAttachment(TRACE_ID);
            String parentId = invocation.getAttachment(SPAN_ID);
            String traceInfo = " (traceId[" + traceId + "] parentId[" + parentId + "])";
            if (e instanceof HttpBusinessException) {
                httpJsonResponse.setStatusCode(((HttpBusinessException) e).getStatusCode());
                httpJsonResponse.setStatusReason(((HttpBusinessException) e).getStatusReason() + traceInfo);
            } else if (e instanceof JsonMappingException) {
                httpJsonResponse.setStatusCode(2);
                httpJsonResponse.setStatusReason("参数映射错误, " + traceInfo + e.getMessage());
            } else {
                httpJsonResponse.setStatusCode(1);
                httpJsonResponse.setStatusReason("调用业务逻辑出错, " + traceInfo + e.getMessage());
            }
            objectMapper.writeValue(response.getWriter(), httpJsonResponse);
        }
    }

    /**
     * 调用目标方法
     */
    private JsonNode invoke(RpcInvocation invocation, String methodName, String paramParameterString) throws Throwable {
        Method method = methodMapping.get(methodName);
        Object[] parameters = PbObjectConvert.convertToObjects(objectMapper, method, paramParameterString);
        invocation.setArguments(parameters);
        invocation.setParameterTypes(method.getParameterTypes());
        Result result = getInvoker().invoke(invocation);
        if (result.hasException()) {
            throw result.getException();
        }
        JsonNode resultJsonNode = PbObjectConvert.convertToJsonNode(objectMapper, result.getValue());
        return resultJsonNode;
    }

    /**
     * 构建RpcInvocation
     *
     * @param methodName 方法名
     * @return
     * @throws JsonProcessingException
     * @throws IOException
     */
    private RpcInvocation constructRpcInvocation(String methodName, HttpServletRequest request) throws IOException {
        RpcInvocation invocation = new RpcInvocation();
        //required
        invocation.setAttachment(INTERFACE_KEY, super.getInvoker().getInterface().getName());
        invocation.setMethodName(methodMapping.get(methodName).getName());
        //option
        invocation.setAttachment(URL_PATH, path + methodName);
        invocation.setAttachment(FROM_IP, request.getRemoteAddr());
        //从http header里解析trace信息
        return invocation;
    }

    /**
     * 提取参数到map <parameterName, value>
     *
     * @param request
     * @return
     * @throws IOException
     */
    private Map<String, String> extractParameters(HttpServletRequest request) throws IOException {
        Map<String, String> parameterMap = new HashMap<String, String>();
        /**
         * 取POST里面的参数
         */
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String payload = IOUtils.read(request.getReader());
            if (payload != null && payload.length() > 0) {
                HttpUtils.splitQuery(parameterMap, payload);
            }
        }
        /**
         * 优先使用GET里面的参数
         */
        Enumeration enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            String paraName = (String) enu.nextElement();
            parameterMap.put(paraName, request.getParameter(paraName));
        }

        if (logger.isInfoEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("request URL: ").append(request.getRequestURL()).append(". ");
            for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
                sb.append("[").append(entry.getKey()).append(" : ").append(entry.getValue()).append("],");
            }
            logger.info(sb.toString());
        }
        return parameterMap;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("HttpJsonExporter ");
        for (String subpath : methodMapping.keySet()) {
            sb.append(path).append(subpath).append(" ");
        }
        return sb.toString();
    }
}
