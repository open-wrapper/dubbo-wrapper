package org.apache.dubbo.common.extension;

import java.net.URL;
import java.util.Objects;

public class SPIMetaInfo {

    private String type;
    private String name;
    private String clsName;
    private java.net.URL resourceURL;
    private ClassLoader loader;
    private SPIStatus status = SPIStatus.MATCH;

    public SPIMetaInfo(String name, String line, String type, URL resourceURL, ClassLoader classLoader) {
        this.name = name;
        this.clsName = line;
        this.type = type;
        this.resourceURL = resourceURL;
        this.loader = classLoader;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClsName() {
        return clsName;
    }

    public void setClsName(String clsName) {
        this.clsName = clsName;
    }

    public URL getResourceURL() {
        return resourceURL;
    }

    public void setResourceURL(URL resourceURL) {
        this.resourceURL = resourceURL;
    }

    public ClassLoader getLoader() {
        return loader;
    }

    public void makeMatch() {
        this.status = SPIStatus.MATCH;
    }

    public void disMatch() {
        this.status = SPIStatus.UN_MATCH;
    }

    public void setLoader(ClassLoader loader) {
        this.loader = loader;
    }

    public SPIStatus getStatus() {
        return status;
    }

    public void setStatus(SPIStatus status) {
        this.status = status;
    }

    public boolean equals(Object o) {
        //no type
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SPIMetaInfo that = (SPIMetaInfo) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(clsName, that.clsName) &&
                Objects.equals(resourceURL, that.resourceURL);
    }

    @Override
    public int hashCode() {
        //no type
        return Objects.hash(name, clsName, resourceURL);
    }
}
