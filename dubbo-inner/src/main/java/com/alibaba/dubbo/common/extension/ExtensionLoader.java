package com.alibaba.dubbo.common.extension;


import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.dubbo.common.URL;

/**
 * @author codel
 * @since 2020-01-16
 */
@Deprecated
public class ExtensionLoader<T> {

    private static final ConcurrentMap<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap();

    private org.apache.dubbo.common.extension.ExtensionLoader<T> loader;

    public ExtensionLoader(org.apache.dubbo.common.extension.ExtensionLoader<T> extensionLoader) {
        this.loader = extensionLoader;
    }


    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        ExtensionLoader loader = EXTENSION_LOADERS.get(type);
        if (loader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader(org.apache.dubbo.common.extension.ExtensionLoader.getExtensionLoader(type)));
            loader = EXTENSION_LOADERS.get(type);
        }
        return loader;
    }

    public static void resetExtensionLoader(Class type) {
        ExtensionLoader loader = EXTENSION_LOADERS.get(type);
        if (loader != null) {
            org.apache.dubbo.common.extension.ExtensionLoader.resetExtensionLoader(type);
            EXTENSION_LOADERS.remove(type);
        }
    }

    public String getExtensionName(T extensionInstance) {
        return loader.getExtensionName(extensionInstance);
    }

    public String getExtensionName(Class<?> extensionClass) {
        return loader.getExtensionName(extensionClass);
    }

    public List<T> getActivateExtension(URL url, String key) {
        return loader.getActivateExtension(url, key);
    }

    public List<T> getActivateExtension(URL url, String[] values) {
        return loader.getActivateExtension(url, values);
    }

    public List<T> getActivateExtension(URL url, String key, String group) {
        return loader.getActivateExtension(url, key, group);
    }

    public List<T> getActivateExtension(URL url, String[] values, String group) {
        return loader.getActivateExtension(url, values, group);
    }

    public T getLoadedExtension(String name) {
        return loader.getLoadedExtension(name);
    }

    public Set<String> getLoadedExtensions() {
        return loader.getLoadedExtensions();
    }

    public Object getLoadedAdaptiveExtensionInstances() {
        return loader.getLoadedAdaptiveExtensionInstances();
    }

    public T getExtension(String name) {
        return loader.getExtension(name);
    }

    public T getDefaultExtension() {
        return loader.getDefaultExtension();
    }

    public boolean hasExtension(String name) {
        return loader.hasExtension(name);
    }

    public Set<String> getSupportedExtensions() {
        return loader.getSupportedExtensions();
    }

    public String getDefaultExtensionName() {
        return loader.getDefaultExtensionName();
    }

    public void addExtension(String name, Class<?> clazz) {
        loader.addExtension(name, clazz);
    }

    public void replaceExtension(String name, Class<?> clazz) {
        loader.replaceExtension(name, clazz);
    }

    public T getAdaptiveExtension() {
        return loader.getAdaptiveExtension();
    }

    @Override
    public String toString() {
        return loader.toString();
    }
}
