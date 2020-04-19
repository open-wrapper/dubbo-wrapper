package com.wrapper.dubbo.common.utils;

import javax.ws.rs.Path;

/**
 * @author codel
 * @since 2020-01-04
 */
public class HttpUtils {
    /**
     * 获取接口设定的path，首先从注解提取，如果提取不到使用package+interfaceName
     * @param interfaceType
     * @return path example: /com/search/ad/userinfo/ (start with '/' and end with '/')
     */
    public static String fetchPath(Class<?> interfaceType){
        notNull(interfaceType);
        //如果设置了path，使用path
        Path pathAnnotation = interfaceType.getAnnotation(Path.class);
        if( pathAnnotation != null ){
            String path = pathAnnotation.value();
            if( path != null && !"".equals(path) ){
                if( !path.startsWith("/") ) path = "/"+path;
                if( !path.endsWith("/") ) path = path + "/";
                return path;
            }
        }
        //如果没有设置path，返回类的路径:package+class
        StringBuilder sb = new StringBuilder();
        sb.append("/");
        sb.append( interfaceType.getName().replaceAll("\\.", "/"));
        sb.append("/");
        return sb.toString();
    }

    /**
     * 如果target为null则抛出异常。
     * @param target
     */
    private static void notNull(Object target) {
        if( target == null ){
            throw new RuntimeException("NullPointerException");
        }
    }
}
