package org.apache.dubbo.rpc.protocol.http;

/**
 * @author codel
 * @since 2020-01-07
 */

import com.alibaba.dubbo.rpc.protocol.util.HttpUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.remoting.http.HttpBinder;
import org.apache.dubbo.remoting.http.HttpServer;
import org.apache.dubbo.rpc.Exporter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.ProxyFactory;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.protocol.AbstractInvoker;
import org.apache.dubbo.rpc.protocol.AbstractProtocol;
import org.apache.dubbo.rpc.protocol.http.converter.HttpJsonRequest;
import org.apache.dubbo.rpc.protocol.http.converter.HttpJsonResponse;
import org.apache.dubbo.rpc.protocol.http.converter.PbObjectConvert;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * HttpProtocol
 */
public class HttpProtocol extends AbstractProtocol {

    private static final Logger logger = LoggerFactory.getLogger(HttpProtocol.class);

    public static final int DEFAULT_PORT = 80;

    private final Map<String, HttpServer> serverMap = new ConcurrentHashMap<String, HttpServer>();
    private final MethodParameterParser parameterParser = MethodParameterParser.getInstance(); //singleton

    private ProxyFactory proxyFactory;  //spi inject
    private HttpBinder httpBinder;      //spi inject

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper = new ObjectMapper();   //singleton

    public HttpProtocol() {
        restTemplate = RestTemplateHelper.getInstance();
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
    }

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        final String path = HttpUtils.fetchPath(invoker.getInterface());
        if (exporterMap.containsKey(path)) {
            return (Exporter<T>) exporterMap.get(path);
        }
        Exporter<T> exporter = new HttpJsonExporter<>(invoker);
        exporterMap.put(path, exporter);
        logger.info(String.format("Exporting interface[%s] at [%s]", invoker.getInterface().getName(), exporter.toString()));

        URL url = invoker.getUrl();
        String address = url.getAddress();
        HttpServer httpServer = serverMap.get(address);
        if (httpServer == null) {
            httpServer = httpBinder.bind(url, new com.alibaba.dubbo.rpc.protocol.http.HttpJsonHandler(exporterMap));
            serverMap.put(address, httpServer);
        }
        return exporter;
    }

    @Override
    protected <T> Invoker<T> protocolBindingRefer(Class<T> type, URL url) throws RpcException {
        T proxy = doRefer(type, url);
        final Invoker<T> target = proxyFactory.getInvoker(proxy, type, url);
        Invoker<T> invoker = new AbstractInvoker<T>(type, url) {
            @Override
            protected Result doInvoke(Invocation invocation) throws Throwable {
                try {
                    Result result = target.invoke(invocation);
                    return result;
                } catch (RpcException e) {
                    if (e.getCode() == RpcException.UNKNOWN_EXCEPTION) {
                        e.setCode(getErrorCode(e.getCause()));
                    }
                    throw e;
                } catch (Throwable e) {
                    throw getRpcException(type, url, invocation, e);
                }
            }
        };
        invokers.add(invoker);
        return invoker;
    }

    @SuppressWarnings("unchecked")
    protected <T> T doRefer(final Class<T> serviceType, final URL url) throws RpcException {
        Class<?>[] interfaceArray = new Class<?>[]{serviceType};
        T proxy = (T) Proxy.newProxyInstance(
                serviceType.getClassLoader(),
                interfaceArray,
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        String protocol = url.getProtocol();
                        String ip = url.getIp();
                        int port = url.getPort();
                        String interfaceName = HttpUtils.fetchPath(serviceType);
                        String methodName = HttpUtils.fetchSubpath(method);

                        String api = interfaceName + methodName;
                        //处理逻辑
                        StringBuilder sb = new StringBuilder();
                        sb.append(protocol).append("://").append(ip).append(":").append(port);
                        sb.append(api.toString());
                        //参数对象转成JSON
                        HttpJsonRequest request = new HttpJsonRequest();
                        request.setBusinessParam(args);
                        String[] parameterNames = parameterParser.parseParameterName(method);
                        String requestString = request.convertToParameter(objectMapper, parameterNames);

                        String responseBody = restTemplate.postForObject(sb.toString(), requestString, String.class);
                        HttpJsonResponse response = objectMapper.readValue(responseBody, new TypeReference<HttpJsonResponse>() {
                        });
                        JsonNode jsonNode = response.getResult();
                        return PbObjectConvert.convertToObject(objectMapper, method.getReturnType(), jsonNode);
                    }
                });

        return proxy;
    }

    protected RpcException getRpcException(Class<?> type, URL url, Invocation invocation, Throwable e) {
        RpcException re = new RpcException("Failed to invoke remote service: " + type + ", method: " + invocation.getMethodName() + ", cause: " + e.getMessage(), e);
        re.setCode(getErrorCode(e));
        return re;
    }

    public void setHttpBinder(HttpBinder httpBinder) {
        this.httpBinder = httpBinder;
    }

    public void setProxyFactory(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    @Override
    public int getDefaultPort() {
        return DEFAULT_PORT;
    }

    protected int getErrorCode(Throwable e) {
        if (e instanceof RemoteAccessException) {
            e = e.getCause();
        }
        if (e != null) {
            Class<?> cls = e.getClass();
            // 是根据测试Case发现的问题，对RpcException.setCode进行设置
            if (SocketTimeoutException.class.equals(cls)) {
                return RpcException.TIMEOUT_EXCEPTION;
            } else if (IOException.class.isAssignableFrom(cls)) {
                return RpcException.NETWORK_EXCEPTION;
            } else if (ClassNotFoundException.class.isAssignableFrom(cls)) {
                return RpcException.SERIALIZATION_EXCEPTION;
            }
        }
        return RpcException.UNKNOWN_EXCEPTION;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
