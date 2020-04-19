package org.apache.dubbo.rpc.protocol.http;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.ws.rs.PathParam;

/**
 * Created by yangwenqi on 16/12/29.
 */
public class MethodParameterParser {

    private final Lock lock = new ReentrantLock();

    private final Map<Method, String[]> methodParameterNameMap = new ConcurrentHashMap<Method, String[]>();

    private MethodParameterParser() {
        super();
    }

    private static class Holder {
        public static final MethodParameterParser INSTANCE = new MethodParameterParser();
    }

    public static MethodParameterParser getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 解析方法参数注解
     *
     * @param method
     * @return
     */
    public String[] parseParameterName(Method method) {
        String[] parameterNames = methodParameterNameMap.get(method);
        if (parameterNames == null) {
            lock.lock();
            try {
                parameterNames = methodParameterNameMap.get(method);
                if (parameterNames == null) {
                    //解析方法类型注解
                    Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                    if (parameterAnnotations != null) {
                        List<String> paramList = new ArrayList<String>(4);
                        for (Annotation[] annotations : parameterAnnotations) {
                            PathParam pathParam = null;
                            for (Annotation annotation : annotations) {
                                if (annotation instanceof PathParam) {
                                    pathParam = (PathParam) annotation;
                                }
                            }
                            if (pathParam != null) {
                                paramList.add(pathParam.value());
                            }
                        }
                        methodParameterNameMap.put(method, paramList.size() == 0 ? new String[]{} : paramList.toArray(new String[]{}));
                    } else {
                        methodParameterNameMap.put(method, new String[]{});
                    }
                    parameterNames = methodParameterNameMap.get(method);
                }
            } finally {
                lock.unlock();
            }
        }
        return parameterNames;
    }
}
