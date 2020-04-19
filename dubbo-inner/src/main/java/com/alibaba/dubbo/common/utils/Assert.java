package com.alibaba.dubbo.common.utils;

/**
 * @author codel
 * @since 2020-01-15
 */
@Deprecated
public abstract class Assert {

    protected Assert() {}

    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

}
