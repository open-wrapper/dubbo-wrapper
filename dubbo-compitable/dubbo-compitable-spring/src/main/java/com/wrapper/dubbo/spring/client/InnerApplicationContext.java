package com.wrapper.dubbo.spring.client;


import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ModuleConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

/**
 * 兼容容器中获得历史bean
 */
public class InnerApplicationContext implements ApplicationContext {

    private ApplicationContext applicationContext;

    public InnerApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    @Override
    public String getId() {
        return applicationContext.getId();
    }

    @Override
    public String getApplicationName() {
        return applicationContext.getApplicationName();
    }

    @Override
    public String getDisplayName() {
        return applicationContext.getDisplayName();
    }

    @Override
    public long getStartupDate() {
        return applicationContext.getStartupDate();
    }

    @Override
    public ApplicationContext getParent() {
        return applicationContext.getParent();
    }

    @Override
    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
        return applicationContext.getAutowireCapableBeanFactory();
    }

    @Override
    public BeanFactory getParentBeanFactory() {
        return applicationContext.getParentBeanFactory();
    }

    @Override
    public boolean containsLocalBean(String s) {
        return applicationContext.containsLocalBean(s);
    }

    @Override
    public boolean containsBeanDefinition(String s) {
        return applicationContext.containsBeanDefinition(s);
    }

    @Override
    public int getBeanDefinitionCount() {
        return applicationContext.getBeanDefinitionCount();
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return applicationContext.getBeanDefinitionNames();
    }

    @Override
    public String[] getBeanNamesForType(ResolvableType resolvableType) {
        return applicationContext.getBeanNamesForType(resolvableType);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> aClass) {
        return applicationContext.getBeanNamesForType(aClass);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> aClass, boolean b, boolean b1) {
        return applicationContext.getBeanNamesForType(aClass, b, b1);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> aClass) throws BeansException {
        return applicationContext.getBeansOfType(aClass);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> aClass, boolean b, boolean b1) throws BeansException {
        return applicationContext.getBeansOfType(aClass, b, b1);
    }

    @Override
    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> aClass) {
        return applicationContext.getBeanNamesForAnnotation(aClass);
    }

    @Override
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> aClass) throws BeansException {
        return applicationContext.getBeansWithAnnotation(aClass);
    }

    @Override
    public <A extends Annotation> A findAnnotationOnBean(String s, Class<A> aClass) throws NoSuchBeanDefinitionException {
        return applicationContext.findAnnotationOnBean(s, aClass);
    }

    @Override
    public Object getBean(String s) throws BeansException {

        return applicationContext.getBean(s);
    }

    @Override
    public <T> T getBean(String s, Class<T> aClass) throws BeansException {
        if (org.apache.dubbo.config.ProtocolConfig.class.isAssignableFrom(aClass)) {
            org.apache.dubbo.config.ProtocolConfig protocolConfig =
                    applicationContext.getBean(s, org.apache.dubbo.config.ProtocolConfig.class);
            ProtocolConfig config = convertProtocolConfig(protocolConfig);
            return (T) config;

        } else if (org.apache.dubbo.config.ApplicationConfig.class.isAssignableFrom(aClass)) {
            org.apache.dubbo.config.ApplicationConfig applicationConfig =
                    applicationContext.getBean(s, org.apache.dubbo.config.ApplicationConfig.class);
            ApplicationConfig config = convertApplicationConfig(applicationConfig);
            return (T) config;

        } else if (org.apache.dubbo.config.ProviderConfig.class.isAssignableFrom(aClass)) {
            org.apache.dubbo.config.ProviderConfig providerConfig =
                    applicationContext.getBean(s, org.apache.dubbo.config.ProviderConfig.class);
            ProviderConfig config = convertProviderConfig(providerConfig);
            return (T) config;

        } else if (org.apache.dubbo.config.ConsumerConfig.class.isAssignableFrom(aClass)) {
            org.apache.dubbo.config.ConsumerConfig consumerConfig =
                    applicationContext.getBean(s, org.apache.dubbo.config.ConsumerConfig.class);
            ConsumerConfig config = convertConsumerConfig(consumerConfig);
            return (T) config;

        } else if (org.apache.dubbo.config.ModuleConfig.class.isAssignableFrom(aClass)) {
            org.apache.dubbo.config.ModuleConfig moduleConfig =
                    applicationContext.getBean(s, org.apache.dubbo.config.ModuleConfig.class);
            ModuleConfig config = convertModulConfig(moduleConfig);
            return (T) config;

        }
        return applicationContext.getBean(s, aClass);
    }

    @Override
    public Object getBean(String s, Object... objects) throws BeansException {
        return applicationContext.getBean(s, objects);
    }

    @Override
    public <T> T getBean(Class<T> aClass) throws BeansException {
        if (org.apache.dubbo.config.ProtocolConfig.class.isAssignableFrom(aClass)) {
            org.apache.dubbo.config.ProtocolConfig protocolConfig =
                    applicationContext.getBean(org.apache.dubbo.config.ProtocolConfig.class);
            ProtocolConfig config = convertProtocolConfig(protocolConfig);
            return (T) config;

        } else if (org.apache.dubbo.config.ApplicationConfig.class.isAssignableFrom(aClass)) {
            org.apache.dubbo.config.ApplicationConfig applicationConfig =
                    applicationContext.getBean(org.apache.dubbo.config.ApplicationConfig.class);
            ApplicationConfig config = convertApplicationConfig(applicationConfig);
            return (T) config;

        } else if (org.apache.dubbo.config.ProviderConfig.class.isAssignableFrom(aClass)) {
            org.apache.dubbo.config.ProviderConfig providerConfig =
                    applicationContext.getBean(org.apache.dubbo.config.ProviderConfig.class);
            ProviderConfig config = convertProviderConfig(providerConfig);
            return (T) config;

        } else if (org.apache.dubbo.config.ConsumerConfig.class.isAssignableFrom(aClass)) {
            org.apache.dubbo.config.ConsumerConfig consumerConfig =
                    applicationContext.getBean(org.apache.dubbo.config.ConsumerConfig.class);
            ConsumerConfig config = convertConsumerConfig(consumerConfig);
            return (T) config;

        } else if (org.apache.dubbo.config.ModuleConfig.class.isAssignableFrom(aClass)) {
            org.apache.dubbo.config.ModuleConfig moduleConfig =
                    applicationContext.getBean(org.apache.dubbo.config.ModuleConfig.class);
            ModuleConfig config = convertModulConfig(moduleConfig);
            return (T) config;

        }
        return applicationContext.getBean(aClass);
    }

    @Override
    public <T> T getBean(Class<T> aClass, Object... objects) throws BeansException {
        return applicationContext.getBean(aClass, objects);
    }

    @Override
    public boolean containsBean(String s) {
        return applicationContext.containsBean(s);
    }

    @Override
    public boolean isSingleton(String s) throws NoSuchBeanDefinitionException {
        return applicationContext.isSingleton(s);
    }

    @Override
    public boolean isPrototype(String s) throws NoSuchBeanDefinitionException {
        return applicationContext.isPrototype(s);
    }

    @Override
    public boolean isTypeMatch(String s, ResolvableType resolvableType) throws NoSuchBeanDefinitionException {
        return applicationContext.isTypeMatch(s, resolvableType);
    }

    @Override
    public boolean isTypeMatch(String s, Class<?> aClass) throws NoSuchBeanDefinitionException {
        return applicationContext.isTypeMatch(s, aClass);
    }

    @Override
    public Class<?> getType(String s) throws NoSuchBeanDefinitionException {
        return applicationContext.getType(s);
    }

    @Override
    public String[] getAliases(String s) {
        return applicationContext.getAliases(s);
    }

    @Override
    public void publishEvent(ApplicationEvent applicationEvent) {
        applicationContext.publishEvent(applicationEvent);
    }

    @Override
    public void publishEvent(Object o) {
        applicationContext.publishEvent(o);
    }

    @Override
    public String getMessage(String s, Object[] objects, String s1, Locale locale) {
        return applicationContext.getMessage(s, objects, s1, locale);
    }

    @Override
    public String getMessage(String s, Object[] objects, Locale locale) throws NoSuchMessageException {
        return applicationContext.getMessage(s, objects, locale);
    }

    @Override
    public String getMessage(MessageSourceResolvable messageSourceResolvable, Locale locale) throws NoSuchMessageException {
        return applicationContext.getMessage(messageSourceResolvable, locale);
    }

    @Override
    public Environment getEnvironment() {
        return applicationContext.getEnvironment();
    }

    @Override
    public Resource[] getResources(String s) throws IOException {
        return applicationContext.getResources(s);
    }

    @Override
    public Resource getResource(String s) {
        return applicationContext.getResource(s);
    }

    @Override
    public ClassLoader getClassLoader() {
        return applicationContext.getClassLoader();
    }

    private static ApplicationConfig convertApplicationConfig(org.apache.dubbo.config.ApplicationConfig applicationConfig) {
        if (applicationConfig == null) {
            return null;
        }
        ApplicationConfig config = new ApplicationConfig();
        config.setName(applicationConfig.getName());
        config.setVersion(applicationConfig.getVersion());
        config.setOwner(applicationConfig.getOwner());
        config.setOrganization(applicationConfig.getOrganization());
        config.setArchitecture(applicationConfig.getArchitecture());
        config.setEnvironment(applicationConfig.getEnvironment());
        org.apache.dubbo.config.RegistryConfig registryConfig = applicationConfig.getRegistry();
        if (registryConfig != null) {
            config.setRegistry(convertRegistryConfig(registryConfig));
        }
        List<org.apache.dubbo.config.RegistryConfig> registryConfigs = applicationConfig.getRegistries();
        if (registryConfigs != null && registryConfigs.size() > 0) {
            List<RegistryConfig> registryConfigs1 = new ArrayList<>();
            for (org.apache.dubbo.config.RegistryConfig registryConfig2 : registryConfigs) {
                registryConfigs1.add(convertRegistryConfig(registryConfig2));
            }
            config.setRegistries(registryConfigs1);
        }

        config.setRegistryIds(applicationConfig.getRegistryIds());
        config.setCompiler(applicationConfig.getCompiler());
        config.setLogger(applicationConfig.getLogger());
        config.setDefault(applicationConfig.isDefault());
        config.setDumpDirectory(applicationConfig.getDumpDirectory());
        config.setQosEnable(applicationConfig.getQosEnable());
        config.setQosHost(applicationConfig.getQosHost());
        config.setQosPort(applicationConfig.getQosPort());
        config.setQosAcceptForeignIp(applicationConfig.getQosAcceptForeignIp());
        config.setQosEnableCompatible(applicationConfig.getQosEnableCompatible());
        config.setQosHostCompatible(applicationConfig.getQosHostCompatible());
        config.setQosPortCompatible(applicationConfig.getQosPortCompatible());
        config.setQosAcceptForeignIpCompatible(applicationConfig.getQosAcceptForeignIpCompatible());
        config.setParameters(applicationConfig.getParameters());
        if (applicationConfig.getShutwait() != null) {
            config.setShutwait(applicationConfig.getShutwait());
        }
        if (applicationConfig.getId() != null)
            config.setId(applicationConfig.getId());
        if (applicationConfig.getPrefix() != null)
            config.setPrefix(applicationConfig.getPrefix());
        return config;
    }


    private static ProtocolConfig convertProtocolConfig(org.apache.dubbo.config.ProtocolConfig protocolConfig) {
        if (protocolConfig == null) {
            return null;
        }
        ProtocolConfig config = new ProtocolConfig();
        if (protocolConfig.getName() != null)
            config.setName(protocolConfig.getName());
        if (protocolConfig.getHost() != null)
            config.setHost(protocolConfig.getHost());
        if (protocolConfig.getPort() != null)
            config.setPort(protocolConfig.getPort());
        if (protocolConfig.getPath() != null)
            config.setPath(protocolConfig.getPath());
        if (protocolConfig.getContextpath() != null)
            config.setContextpath(protocolConfig.getContextpath());
        if (protocolConfig.getThreadpool() != null)
            config.setThreadpool(protocolConfig.getThreadpool());
        if (protocolConfig.getCorethreads() != null)
            config.setCorethreads(protocolConfig.getCorethreads());
        if (protocolConfig.getThreads() != null)
            config.setThreads(protocolConfig.getThreads());
        if (protocolConfig.getIothreads() != null)
            config.setIothreads(protocolConfig.getIothreads());
        if (protocolConfig.getQueues() != null)
            config.setQueues(protocolConfig.getQueues());
        if (protocolConfig.getAccepts() != null)
            config.setAccepts(protocolConfig.getAccepts());
        if (protocolConfig.getCodec() != null)
            config.setCodec(protocolConfig.getCodec());
        if (protocolConfig.getSerialization() != null)
            config.setSerialization(protocolConfig.getSerialization());
        if (protocolConfig.getCharset() != null)
            config.setCharset(protocolConfig.getCharset());
        if (protocolConfig.getPayload() != null)
            config.setPayload(protocolConfig.getPayload());
        if (protocolConfig.getBuffer() != null)
            config.setBuffer(protocolConfig.getBuffer());
        if (protocolConfig.getHeartbeat() != null)
            config.setHeartbeat(protocolConfig.getHeartbeat());
        if (protocolConfig.getServer() != null)
            config.setServer(protocolConfig.getServer());
        if (protocolConfig.getClient() != null)
            config.setClient(protocolConfig.getClient());
        if (protocolConfig.getAccesslog() != null)
            config.setAccesslog(protocolConfig.getAccesslog());
        if (protocolConfig.getTelnet() != null)
            config.setTelnet(protocolConfig.getTelnet());
        if (protocolConfig.getPrompt() != null)
            config.setPrompt(protocolConfig.getPrompt());
        if (protocolConfig.getStatus() != null)
            config.setStatus(protocolConfig.getStatus());
        if (protocolConfig.isRegister() != null)
            config.setRegister(protocolConfig.isRegister());
        if (protocolConfig.getTransporter() != null)
            config.setTransporter(protocolConfig.getTransporter());
        if (protocolConfig.getExchanger() != null)
            config.setExchanger(protocolConfig.getExchanger());
        if (protocolConfig.getDispather() != null)
            config.setDispather(protocolConfig.getDispather());
        if (protocolConfig.getDispatcher() != null)
            config.setDispatcher(protocolConfig.getDispatcher());
        if (protocolConfig.getNetworker() != null)
            config.setNetworker(protocolConfig.getNetworker());
        if (protocolConfig.getParameters() != null)
            config.setParameters(protocolConfig.getParameters());
        if (protocolConfig.isDefault() != null)
            config.setDefault(protocolConfig.isDefault());
        if (protocolConfig.getKeepAlive() != null)
            config.setKeepAlive(protocolConfig.getKeepAlive());
        if (protocolConfig.getOptimizer() != null)
            config.setOptimizer(protocolConfig.getOptimizer());
        if (protocolConfig.getExtension() != null)
            config.setExtension(protocolConfig.getExtension());
        if (protocolConfig.getId() != null)
            config.setId(protocolConfig.getId());
        if (protocolConfig.getPrefix() != null)
            config.setPrefix(protocolConfig.getPrefix());
        return config;

    }


    private static RegistryConfig convertRegistryConfig(org.apache.dubbo.config.RegistryConfig registryConfig) {
        if (registryConfig == null) {
            return null;
        }
        RegistryConfig config = new RegistryConfig();
        if (registryConfig.getProtocol() != null)
            config.setProtocol(registryConfig.getProtocol());
        if (registryConfig.getAddress() != null)
            config.setAddress(registryConfig.getAddress());
        if (registryConfig.getPort() != null)
            config.setPort(registryConfig.getPort());
        if (registryConfig.getUsername() != null)
            config.setUsername(registryConfig.getUsername());
        if (registryConfig.getPassword() != null)
            config.setPassword(registryConfig.getPassword());
        if (registryConfig.getWait() != null)
            config.setWait(registryConfig.getWait());
        if (registryConfig.isCheck() != null)
            config.setCheck(registryConfig.isCheck());
        if (registryConfig.getFile() != null)
            config.setFile(registryConfig.getFile());
        if (registryConfig.getTransport() != null)
            config.setTransport(registryConfig.getTransport());
        if (registryConfig.getTransporter() != null)
            config.setTransporter(registryConfig.getTransporter());
        if (registryConfig.getServer() != null)
            config.setServer(registryConfig.getServer());
        if (registryConfig.getClient() != null)
            config.setClient(registryConfig.getClient());
        if (registryConfig.getTimeout() != null)
            config.setTimeout(registryConfig.getTimeout());
        if (registryConfig.getSession() != null)
            config.setSession(registryConfig.getSession());
        if (registryConfig.isDynamic() != null)
            config.setDynamic(registryConfig.isDynamic());
        if (registryConfig.isRegister() != null)
            config.setRegister(registryConfig.isRegister());
        if (registryConfig.isSubscribe() != null)
            config.setSubscribe(registryConfig.isSubscribe());
        if (registryConfig.getCluster() != null)
            config.setCluster(registryConfig.getCluster());
        if (registryConfig.getGroup() != null)
            config.setGroup(registryConfig.getGroup());
        if (registryConfig.getVersion() != null)
            config.setVersion(registryConfig.getVersion());
        if (registryConfig.getParameters() != null)
            config.setParameters(registryConfig.getParameters());
        if (registryConfig.isDefault() != null)
            config.setDefault(registryConfig.isDefault());
        if (registryConfig.getSimplified() != null)
            config.setSimplified(registryConfig.getSimplified());
        if (registryConfig.getExtraKeys() != null)
            config.setExtraKeys(registryConfig.getExtraKeys());
        if (registryConfig.getId() != null)
            config.setId(registryConfig.getId());
        if (registryConfig.getPrefix() != null)
            config.setPrefix(registryConfig.getPrefix());
        return config;

    }

    private static ProviderConfig convertProviderConfig(org.apache.dubbo.config.ProviderConfig providerConfig) {
        if (providerConfig == null) {
            return null;
        }
        ProviderConfig config = new ProviderConfig();
        org.apache.dubbo.config.ProtocolConfig protocolConfig = providerConfig.getProtocol();
        if (protocolConfig != null) {
            config.setProtocol(protocolConfig.getName());
        }
        config.setDefault(providerConfig.isDefault());
        config.setHost(providerConfig.getHost());
        config.setPort(providerConfig.getPort());
        config.setPath(providerConfig.getPath());
        if (providerConfig.getContextpath() != null)
            config.setContextpath(providerConfig.getContextpath());
        if (providerConfig.getThreadpool() != null)
            config.setThreadpool(providerConfig.getThreadpool());
        if (providerConfig.getThreads() != null)
            config.setThreads(providerConfig.getThreads());
        if (providerConfig.getIothreads() != null)
            config.setIothreads(providerConfig.getIothreads());
        if (providerConfig.getQueues() != null)
            config.setQueues(providerConfig.getQueues());
        if (providerConfig.getAccepts() != null)
            config.setAccepts(providerConfig.getAccepts());
        if (providerConfig.getCodec() != null)
            config.setCodec(providerConfig.getCodec());
        if (providerConfig.getCharset() != null)
            config.setCharset(providerConfig.getCharset());
        if (providerConfig.getPayload() != null)
            config.setPayload(providerConfig.getPayload());
        if (providerConfig.getBuffer() != null)
            config.setBuffer(providerConfig.getBuffer());
        if (providerConfig.getServer() != null)
            config.setServer(providerConfig.getServer());
        if (providerConfig.getClient() != null)
            config.setClient(providerConfig.getClient());
        if (providerConfig.getTelnet() != null)
            config.setTelnet(providerConfig.getTelnet());
        if (providerConfig.getPrompt() != null)
            config.setPrompt(providerConfig.getPrompt());
        if (providerConfig.getStatus() != null)
            config.setStatus(providerConfig.getStatus());
        if (providerConfig.getTransporter() != null)
            config.setTransporter(providerConfig.getTransporter());
        if (providerConfig.getExchanger() != null)
            config.setExchanger(providerConfig.getExchanger());
        if (providerConfig.getDispather() != null)
            config.setDispather(providerConfig.getDispather());
        if (providerConfig.getDispatcher() != null)
            config.setDispatcher(providerConfig.getDispatcher());
        if (providerConfig.getNetworker() != null)
            config.setNetworker(providerConfig.getNetworker());
        if (providerConfig.getWait() != null)
            config.setWait(providerConfig.getWait());
        if (providerConfig.getVersion() != null)
            config.setVersion(providerConfig.getVersion());
        if (providerConfig.getGroup() != null)
            config.setGroup(providerConfig.getGroup());
        if (providerConfig.getDelay() != null)
            config.setDelay(providerConfig.getDelay());
        if (providerConfig.getExport() != null)
            config.setExport(providerConfig.getExport());
        if (providerConfig.getWeight() != null)
            config.setWeight(providerConfig.getWeight());
        if (providerConfig.getDocument() != null)
            config.setDocument(providerConfig.getDocument());
        if (providerConfig.getToken() != null)
            config.setToken(providerConfig.getToken());
        if (providerConfig.isDeprecated() != null)
            config.setDeprecated(providerConfig.isDeprecated());
        if (providerConfig.isDynamic() != null)
            config.setDynamic(providerConfig.isDynamic());
        if (providerConfig.getProtocolIds() != null)
            config.setProtocolIds(providerConfig.getProtocolIds());
        if (providerConfig.getAccesslog() != null)
            config.setAccesslog(providerConfig.getAccesslog());
        if (providerConfig.getExecutes() != null)
            config.setExecutes(providerConfig.getExecutes());
        if (providerConfig.getListener() != null)
            config.setListener(providerConfig.getListener());
        if (providerConfig.isRegister() != null)
            config.setRegister(providerConfig.isRegister());
        if (providerConfig.getWarmup() != null)
            config.setWarmup(providerConfig.getWarmup());
        if (providerConfig.getSerialization() != null)
            config.setSerialization(providerConfig.getSerialization());
        if (providerConfig.getLocal() != null)
            config.setLocal(providerConfig.getLocal());
        if (providerConfig.getStub() != null)
            config.setStub(providerConfig.getStub());
        if (providerConfig.getCluster() != null)
            config.setCluster(providerConfig.getCluster());
        if (providerConfig.getProxy() != null)
            config.setProxy(providerConfig.getProxy());
        if (providerConfig.getConnections() != null)
            config.setConnections(providerConfig.getConnections());
        if (providerConfig.getFilter() != null)
            config.setFilter(providerConfig.getFilter());
        if (providerConfig.getListener() != null)
            config.setListener(providerConfig.getListener());
        if (providerConfig.getLayer() != null)
            config.setLayer(providerConfig.getLayer());
        org.apache.dubbo.config.ApplicationConfig applicationConfig = providerConfig.getApplication();
        if (applicationConfig != null) {
            config.setApplication(convertApplicationConfig(applicationConfig));
        }
        org.apache.dubbo.config.ModuleConfig moduleConfig = providerConfig.getModule();
        if (moduleConfig != null) {
            config.setModule(convertModulConfig(moduleConfig));
        }
        org.apache.dubbo.config.RegistryConfig registryConfig = providerConfig.getRegistry();
        if (registryConfig != null) {
            config.setRegistry(convertRegistryConfig(registryConfig));
        }
        List<org.apache.dubbo.config.RegistryConfig> registryConfigs = providerConfig.getRegistries();
        if (registryConfigs != null && registryConfigs.size() > 0) {
            List<RegistryConfig> registryConfigs1 = new ArrayList<>();
            for (org.apache.dubbo.config.RegistryConfig registryConfig2 : registryConfigs) {
                registryConfigs1.add(convertRegistryConfig(registryConfig2));
            }
            config.setRegistries(registryConfigs1);
        }
        if (providerConfig.getRegistryIds() != null)
            config.setRegistryIds(providerConfig.getRegistryIds());
        if (providerConfig.getMonitor() != null)
            config.setMonitor(providerConfig.getMonitor());
        if (providerConfig.getOwner() != null)
            config.setOwner(providerConfig.getOwner());
        if (providerConfig.getConfigCenter() != null)
            config.setConfigCenter(providerConfig.getConfigCenter());
        if (providerConfig.getCallbacks() != null)
            config.setCallbacks(providerConfig.getCallbacks());
        if (providerConfig.getOnconnect() != null)
            config.setOnconnect(providerConfig.getOnconnect());
        if (providerConfig.getOndisconnect() != null)
            config.setOndisconnect(providerConfig.getOndisconnect());
        if (providerConfig.getScope() != null)
            config.setScope(providerConfig.getScope());
        if (providerConfig.getMetadataReportConfig() != null)
            config.setMetadataReportConfig(providerConfig.getMetadataReportConfig());
        if (providerConfig.getMetrics() != null)
            config.setMetrics(providerConfig.getMetrics());
        if (providerConfig.getTag() != null)
            config.setTag(providerConfig.getTag());
        if (providerConfig.getForks() != null)
            config.setForks(providerConfig.getForks());
        if (providerConfig.getTimeout() != null)
            config.setTimeout(providerConfig.getTimeout());
        if (providerConfig.getRetries() != null)
            config.setRetries(providerConfig.getRetries());
        if (providerConfig.getLoadbalance() != null)
            config.setLoadbalance(providerConfig.getLoadbalance());
        if (providerConfig.isAsync() != null)
            config.setAsync(providerConfig.isAsync());
        if (providerConfig.getActives() != null)
            config.setActives(providerConfig.getActives());
        if (providerConfig.getSent() != null)
            config.setSent(providerConfig.getSent());
        if (providerConfig.getMock() != null)
            config.setMock(providerConfig.getMock());
        if (providerConfig.getMerger() != null)
            config.setMerger(providerConfig.getMerger());
        if (providerConfig.getCache() != null)
            config.setCache(providerConfig.getCache());
        if (providerConfig.getValidation() != null)
            config.setValidation(providerConfig.getValidation());
        if (providerConfig.getParameters() != null)
            config.setParameters(providerConfig.getParameters());
        if (providerConfig.getId() != null)
            config.setId(providerConfig.getId());
        if (providerConfig.getThreads() != null)
            config.setPrefix(providerConfig.getPrefix());
        return config;

    }

    private static ConsumerConfig convertConsumerConfig(org.apache.dubbo.config.ConsumerConfig consumerConfig) {
        if (consumerConfig == null) {
            return null;
        }
        ConsumerConfig config = new ConsumerConfig();
        if (consumerConfig.getTimeout() != null)
            config.setTimeout(consumerConfig.getTimeout());
        if (consumerConfig.getClient() != null)
            config.setClient(consumerConfig.getClient());
        if (consumerConfig.getThreadpool() != null)
            config.setThreadpool(consumerConfig.getThreadpool());
        if (consumerConfig.getDefault() != null)
            config.setDefault(consumerConfig.getDefault());
        if (consumerConfig.getCorethreads() != null)
            config.setCorethreads(consumerConfig.getCorethreads());
        if (consumerConfig.getThreads() != null)
            config.setThreads(consumerConfig.getThreads());
        if (consumerConfig.getQueues() != null)
            config.setQueues(consumerConfig.getQueues());
        if (consumerConfig.getShareconnections() != null)
            config.setShareconnections(consumerConfig.getShareconnections());
        if (consumerConfig.isCheck() != null)
            config.setCheck(consumerConfig.isCheck());
        if (consumerConfig.isInit() != null)
            config.setInit(consumerConfig.isInit());
        if (consumerConfig.getGeneric() != null)
            config.setGeneric(consumerConfig.getGeneric());
        if (consumerConfig.isInjvm() != null)
            config.setInjvm(consumerConfig.isInjvm());
        if (consumerConfig.getListener() != null)
            config.setListener(consumerConfig.getListener());
        if (consumerConfig.getLazy() != null)
            config.setLazy(consumerConfig.getLazy());
        if (consumerConfig.getOnconnect() != null)
            config.setOnconnect(consumerConfig.getOnconnect());
        if (consumerConfig.getOndisconnect() != null)
            config.setOndisconnect(consumerConfig.getOndisconnect());
        if (consumerConfig.getReconnect() != null)
            config.setReconnect(consumerConfig.getReconnect());
        if (consumerConfig.getSticky() != null)
            config.setSticky(consumerConfig.getSticky());
        if (consumerConfig.getVersion() != null)
            config.setVersion(consumerConfig.getVersion());
        if (consumerConfig.getGroup() != null)
            config.setGroup(consumerConfig.getGroup());
        if (consumerConfig.getLocal() != null)
            config.setLocal(consumerConfig.getLocal());
        if (consumerConfig.getStub() != null)
            config.setStub(consumerConfig.getStub());
        if (consumerConfig.getCluster() != null)
            config.setCluster(consumerConfig.getCluster());
        if (consumerConfig.getProxy() != null)
            config.setProxy(consumerConfig.getProxy());
        if (consumerConfig.getConnections() != null)
            config.setConnections(consumerConfig.getConnections());
        if (consumerConfig.getFilter() != null)
            config.setFilter(consumerConfig.getFilter());
        if (consumerConfig.getListener() != null)
            config.setListener(consumerConfig.getListener());
        if (consumerConfig.getLayer() != null)
            config.setLayer(consumerConfig.getLayer());
        org.apache.dubbo.config.ApplicationConfig applicationConfig = consumerConfig.getApplication();
        if (applicationConfig != null) {
            config.setApplication(convertApplicationConfig(applicationConfig));
        }
        org.apache.dubbo.config.ModuleConfig moduleConfig = consumerConfig.getModule();
        if (moduleConfig != null) {
            config.setModule(convertModulConfig(moduleConfig));
        }
        org.apache.dubbo.config.RegistryConfig registryConfig = consumerConfig.getRegistry();
        if (registryConfig != null) {
            config.setRegistry(convertRegistryConfig(registryConfig));
        }
        List<org.apache.dubbo.config.RegistryConfig> registryConfigs = consumerConfig.getRegistries();
        if (registryConfigs != null && registryConfigs.size() > 0) {
            List<RegistryConfig> registryConfigs1 = new ArrayList<>();
            for (org.apache.dubbo.config.RegistryConfig registryConfig2 : registryConfigs) {
                registryConfigs1.add(convertRegistryConfig(registryConfig2));
            }
            config.setRegistries(registryConfigs1);
        }
        if (consumerConfig.getRegistryIds() != null)
            config.setRegistryIds(consumerConfig.getRegistryIds());
        if (consumerConfig.getOwner() != null)
            config.setOwner(consumerConfig.getOwner());
        if (consumerConfig.getConfigCenter() != null)
            config.setConfigCenter(consumerConfig.getConfigCenter());
        if (consumerConfig.getCallbacks() != null)
            config.setCallbacks(consumerConfig.getCallbacks());
        if (consumerConfig.getOnconnect() != null)
            config.setOnconnect(consumerConfig.getOnconnect());
        if (consumerConfig.getOndisconnect() != null)
            config.setOndisconnect(consumerConfig.getOndisconnect());
        if (consumerConfig.getScope() != null)
            config.setScope(consumerConfig.getScope());
        if (consumerConfig.getMetadataReportConfig() != null)
            config.setMetadataReportConfig(consumerConfig.getMetadataReportConfig());
        if (consumerConfig.getMetrics() != null)
            config.setMetrics(consumerConfig.getMetrics());
        if (consumerConfig.getTag() != null)
            config.setTag(consumerConfig.getTag());
        if (consumerConfig.getForks() != null)
            config.setForks(consumerConfig.getForks());
        if (consumerConfig.getTimeout() != null)
            config.setTimeout(consumerConfig.getTimeout());
        if (consumerConfig.getRetries() != null)
            config.setRetries(consumerConfig.getRetries());
        if (consumerConfig.getLoadbalance() != null)
            config.setLoadbalance(consumerConfig.getLoadbalance());
        if (consumerConfig.isAsync() != null)
            config.setAsync(consumerConfig.isAsync());
        if (consumerConfig.getActives() != null)
            config.setActives(consumerConfig.getActives());
        if (consumerConfig.getSent() != null)
            config.setSent(consumerConfig.getSent());
        if (consumerConfig.getMock() != null)
            config.setMock(consumerConfig.getMock());
        if (consumerConfig.getMerger() != null)
            config.setMerger(consumerConfig.getMerger());
        if (consumerConfig.getCache() != null)
            config.setCache(consumerConfig.getCache());
        if (consumerConfig.getValidation() != null)
            config.setValidation(consumerConfig.getValidation());
        if (consumerConfig.getParameters() != null)
            config.setParameters(consumerConfig.getParameters());
        if (consumerConfig.getId() != null)
            config.setId(consumerConfig.getId());
        if (consumerConfig.getPrefix() != null)
            config.setPrefix(consumerConfig.getPrefix());
        return config;

    }

    private static ModuleConfig convertModulConfig(org.apache.dubbo.config.ModuleConfig moduleConfig) {
        if (moduleConfig == null) {
            return null;
        }
        ModuleConfig config = new ModuleConfig();
        if (moduleConfig.getName() != null)
            config.setName(moduleConfig.getName());
        config.setVersion(moduleConfig.getVersion());
        config.setOwner(moduleConfig.getOwner());
        config.setOrganization(moduleConfig.getOrganization());
        org.apache.dubbo.config.RegistryConfig registryConfig = moduleConfig.getRegistry();
        if (registryConfig != null) {
            config.setRegistry(convertRegistryConfig(registryConfig));
        }
        List<org.apache.dubbo.config.RegistryConfig> registryConfigs = moduleConfig.getRegistries();
        if (registryConfigs != null && registryConfigs.size() > 0) {
            List<RegistryConfig> registryConfigs1 = new ArrayList<>();
            for (org.apache.dubbo.config.RegistryConfig registryConfig2 : registryConfigs) {
                registryConfigs1.add(convertRegistryConfig(registryConfig2));
            }
            config.setRegistries(registryConfigs1);
        }
        if (moduleConfig.isDefault() != null)
            config.setDefault(moduleConfig.isDefault());
        if (moduleConfig.getId() != null)
            config.setId(moduleConfig.getId());
        if (moduleConfig.getPrefix() != null)
            config.setPrefix(moduleConfig.getPrefix());
        return config;
    }


}
