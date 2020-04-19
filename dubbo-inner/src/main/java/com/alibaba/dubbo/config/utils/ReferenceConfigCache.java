package com.alibaba.dubbo.config.utils;


/**
 * @author codel
 * @since 2020-01-13
 */
@Deprecated
public class ReferenceConfigCache {

    public static final String DEFAULT_NAME = "_DEFAULT_";

    private org.apache.dubbo.config.utils.ReferenceConfigCache referenceConfigCache;


    public ReferenceConfigCache(org.apache.dubbo.config.utils.ReferenceConfigCache referenceConfigCache) {
        this.referenceConfigCache = referenceConfigCache;
    }

    public static ReferenceConfigCache getCache() {
        org.apache.dubbo.config.utils.ReferenceConfigCache cache1 = org.apache.dubbo.config.utils.ReferenceConfigCache.getCache(DEFAULT_NAME);
        if (cache1 == null) {
            return null;
        }
        return new ReferenceConfigCache(cache1);
    }

    public static ReferenceConfigCache getCache(String name) {
        org.apache.dubbo.config.utils.ReferenceConfigCache cache1 = org.apache.dubbo.config.utils.ReferenceConfigCache.getCache(name);
        if (cache1 == null) {
            return null;
        }
        return new ReferenceConfigCache(cache1);
    }


    @SuppressWarnings("unchecked")
    public <T> T get(com.alibaba.dubbo.config.ReferenceConfig<T> referenceConfig) {
        return referenceConfigCache.get(referenceConfig);
    }

    public <T> void destroy(com.alibaba.dubbo.config.ReferenceConfig<T> referenceConfig) {
        referenceConfigCache.destroy(referenceConfig);
    }

    public void destroyAll() {
        referenceConfigCache.destroyAll();
    }

    @Override
    public String toString() {
        return referenceConfigCache.toString();
    }
}
