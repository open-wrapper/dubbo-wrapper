package com.alibaba.dubbo.rpc.protocol.http;


import com.alibaba.dubbo.rpc.protocol.http.converter.Status;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.remoting.http.HttpHandler;
import org.apache.dubbo.rpc.Exporter;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.protocol.http.HttpJsonExporter;

public class HttpJsonHandler implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(HttpJsonHandler.class);

    private Map<String, Exporter<?>> exporterMap;

    private static final ThreadLocal<Status> StatusThreadLocal = new ThreadLocal<Status>();

    private static final ThreadLocal<String> JsonpThreadLocal = new ThreadLocal<String>();

    private static final ThreadLocal<HttpServletRequest> RequestThreadLocal = new ThreadLocal<HttpServletRequest>();

    private static final ThreadLocal<HttpServletResponse> ResponseThreadLocal = new ThreadLocal<HttpServletResponse>();

    //保存post请求的参数, 或get请求的param/public参数, by wuyanfei, 20170518
    private static final ThreadLocal<Map<String, String>> ParametersLocal = new ThreadLocal<Map<String, String>>();

    //是否输出用户自定义的response，默认false(输出框架的HttpJsonResponse), by wuyanfei, 20170519
    private static final ThreadLocal<Boolean> UserDefinedResponseFlag = new ThreadLocal<Boolean>();

    public HttpJsonHandler(Map<String, Exporter<?>> exporterMap) {
        this.exporterMap = exporterMap;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException,
            ServletException {
        RpcContext.getContext().setRemoteAddress(request.getRemoteAddr(), request.getRemotePort());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=utf8");
        try {
            String pathInfo = request.getRequestURI();

            if ("/favicon.ico".equalsIgnoreCase(pathInfo)) {
                return;
            }
            String interfaceName = getInterfaceName(pathInfo);
            String methodName = getMehtodName(pathInfo);
            RequestThreadLocal.set(request);
            ResponseThreadLocal.set(response);
            HttpJsonExporter<?> exporter = (HttpJsonExporter<?>) exporterMap.get(interfaceName);
            if (exporter == null) {
                //服务接口不存在
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Service not found.");
            } else {
                exporter.handleRequest(methodName, request, response);
            }
        } catch (Throwable e) {
            logger.info("", e);
            throw new ServletException(e);
        }
    }

    private String getMehtodName(String pathInfo) {
        int lastSlashIndex = pathInfo.lastIndexOf("/");
        int lastDotIndex = pathInfo.lastIndexOf(".");   //ignore the extension
        if (lastDotIndex != -1 && lastDotIndex > lastSlashIndex) {
            return pathInfo.substring(lastSlashIndex + 1, lastDotIndex);
        } else {
            return pathInfo.substring(lastSlashIndex + 1);
        }

    }

    private String getInterfaceName(String pathInfo) {
        int lastIndex = pathInfo.lastIndexOf("/");
        return pathInfo.substring(0, lastIndex + 1);
    }


    public static HttpServletRequest getHttpServletRequest() {
        return RequestThreadLocal.get();
    }

    public static HttpServletResponse getHttpServletResponse(){
        return ResponseThreadLocal.get();
    }

    public static void setStatus(Status status) {
        StatusThreadLocal.set(status);
    }

    public static Status getStatus() {
        return StatusThreadLocal.get();
    }

    public static void setJsonpMethod(String jsonpMethod) {
        JsonpThreadLocal.set(jsonpMethod);
    }

    public static String getJsonpMethod() {
        return JsonpThreadLocal.get();
    }

    public static void setParameters(Map<String, String> map) {
        ParametersLocal.set(map);
    }

    public static Boolean getUserDefinedResponseFlag() {
        return UserDefinedResponseFlag.get();
    }

    public static void setUserDefinedResponseFlag(Boolean flag) {
        UserDefinedResponseFlag.set(flag);
    }

    public static Map<String, String> getParameters() {
        return ParametersLocal.get();
    }

    /**
     * 清threadlocal相关变量
     */
    public static void clear() {
        StatusThreadLocal.remove();
        JsonpThreadLocal.remove();
        ParametersLocal.remove();
        UserDefinedResponseFlag.remove();
    }
}
