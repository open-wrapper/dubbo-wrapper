package com.alibaba.dubbo.rpc.protocol.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.Map;
import javax.ws.rs.Path;
import org.springframework.util.StringUtils;


public class HttpUtils {

    /**
     * 解析request参数param={json data}&public={json data}，返回map
     *
     * @param queryString
     * @return
     * @throws UnsupportedEncodingException
     */
    public static void splitQuery(Map<String, String> paramsMap, String queryString) throws UnsupportedEncodingException {
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            paramsMap.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
    }

    /**
     * 获取接口设定的path，首先从注解提取，如果提取不到使用package+interfaceName
     *
     * @param interfaceType
     * @return path example: /com/search/ad/userinfo/ (start with '/' and end with '/')
     */
    public static String fetchPath(Class<?> interfaceType) {
        notNull(interfaceType);
        //如果设置了path，使用path
        Path pathAnnotation = interfaceType.getAnnotation(Path.class);
        if (pathAnnotation != null) {
            String path = pathAnnotation.value();
            if (!StringUtils.isEmpty(path)) {
                if (!path.startsWith("/")) path = "/" + path;
                if (!path.endsWith("/")) path = path + "/";
                return path;
            }
        }
        //如果没有设置path，返回类的路径:package+class
        StringBuilder sb = new StringBuilder();
        sb.append("/");
        sb.append(interfaceType.getName().replaceAll("\\.", "/"));
        sb.append("/");
        return sb.toString();
    }

    /**
     * 提取方法设定的path，首先从注解提取，如果提取不到使用方法名称
     *
     * @param method
     * @return subpath example：getId
     */
    public static String fetchSubpath(Method method) {
        notNull(method);
        Path pathAnnotation = method.getAnnotation(Path.class);
        if (pathAnnotation != null) {
            String subPath = pathAnnotation.value();
            if (!StringUtils.isEmpty(subPath)) {
                if (subPath.startsWith("/")) subPath = subPath.substring(1);
                if (subPath.endsWith("/")) subPath = subPath.substring(0, subPath.length() - 1);
                return subPath;
            }
        }
        //返回方法名称
        return method.getName();
    }

    /**
     * 如果target为null则抛出异常。
     *
     * @param target
     */
    private static void notNull(Object target) {
        if (target == null) {
            throw new RuntimeException("NullPointerException");
        }
    }

}
