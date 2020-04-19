/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.dubbo.config;

import org.apache.dubbo.config.annotation.Service;

@Deprecated
public class ServiceConfig<T> extends org.apache.dubbo.config.ServiceConfig<T> {

    public ServiceConfig() {
    }

    public ServiceConfig(Service service) {
        super(service);
    }

    public void setProvider(ProviderConfig provider) {
        super.setProvider(provider);
    }


    public void setApplication(ApplicationConfig application) {
        super.setApplication(application);
    }

    public void setModule(ModuleConfig module) {
        super.setModule(module);
    }

    public void setMonitor(MonitorConfig monitor) {
        super.setMonitor(monitor);
    }

    public void setProtocol(ProtocolConfig protocol) {
        super.setProtocol(protocol);
    }

    public void setRegistry(RegistryConfig registry) {
        super.setRegistry(registry);
    }

    @Override
    public ProtocolConfig getProtocol() {
        return convertProtocolConfig(super.getProtocol());
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

}
