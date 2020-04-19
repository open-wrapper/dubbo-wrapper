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

package com.alibaba.dubbo.rpc;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.AsyncContext;
import org.apache.dubbo.rpc.FutureContext;

import com.alibaba.dubbo.rpc.protocol.dubbo.FutureAdapter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;

@Deprecated
public class RpcContext extends org.apache.dubbo.rpc.RpcContext {

    private org.apache.dubbo.rpc.RpcContext rpcInnerContext;

    private void setRpcInnerContext(org.apache.dubbo.rpc.RpcContext rpcInnerContext) {
        this.rpcInnerContext = rpcInnerContext;
    }

    public static RpcContext getContext() {
        RpcContext rpcContext = newInstance(org.apache.dubbo.rpc.RpcContext.getContext());
        return rpcContext;
    }

    private static RpcContext newInstance(org.apache.dubbo.rpc.RpcContext rpcContext) {
        if (rpcContext == null) {
            return null;
        }
        if (RpcContext.class.isAssignableFrom(rpcContext.getClass())) {
            return (RpcContext) rpcContext;
        }
        RpcContext copy = new RpcContext();
        copy.setRpcInnerContext(rpcContext);
        return copy;
    }

    @Override
    public <T> Future<T> getFuture() {
        CompletableFuture completableFuture = FutureContext.getContext().getCompatibleCompletableFuture();
        if (completableFuture == null) {
            return null;
        }
        return new FutureAdapter(completableFuture);
    }

    @Override
    public void setFuture(CompletableFuture<?> future) {
        FutureContext.getContext().setCompatibleFuture(future);
    }

    @Override
    public RpcContext remove(String key) {
        if (rpcInnerContext != null) rpcInnerContext.remove(key);
        return this;
    }

    @Override
    public RpcContext removeAttachment(String key) {
        if (rpcInnerContext != null) rpcInnerContext.removeAttachment(key);
        return this;
    }

    @Override
    public RpcContext set(String key, Object value) {
        if (rpcInnerContext != null) rpcInnerContext.set(key, value);
        return this;
    }

    @Override
    public RpcContext setAttachment(String key, String value) {
        if (rpcInnerContext != null) rpcInnerContext.setAttachment(key, value);
        return this;
    }

    @Override
    public RpcContext setAttachments(Map<String, String> attachment) {
        if (rpcInnerContext != null) rpcInnerContext.setAttachments(attachment);
        return this;
    }

    @Override
    public RpcContext setInvocation(Invocation invocation) {
        if (rpcInnerContext != null) rpcInnerContext.setInvocation(invocation);
        return this;
    }

    @Override
    public RpcContext setInvokers(List<Invoker<?>> invokers) {
        if (rpcInnerContext != null) rpcInnerContext.setInvokers(invokers);
        return this;
    }

    @Override
    public RpcContext setLocalAddress(InetSocketAddress address) {
        if (rpcInnerContext != null) rpcInnerContext.setLocalAddress(address);
        return this;
    }

    @Override
    public RpcContext setLocalAddress(String host, int port) {
        if (rpcInnerContext != null) rpcInnerContext.setLocalAddress(host, port);
        return this;
    }


    @Override
    public RpcContext setRemoteAddress(InetSocketAddress address) {
        if (rpcInnerContext != null) rpcInnerContext.setRemoteAddress(address);
        return this;
    }


    @Override
    public RpcContext setRemoteAddress(String host, int port) {
        if (rpcInnerContext != null) rpcInnerContext.setRemoteAddress(host, port);
        return this;
    }

    @Override
    public RpcContext setInvoker(Invoker<?> invoker) {
        if (rpcInnerContext != null) rpcInnerContext.setInvoker(invoker);
        return this;
    }

    @Override
    public RpcContext setRemoteApplicationName(String remoteApplicationName) {
        if (rpcInnerContext != null) rpcInnerContext.setRemoteApplicationName(remoteApplicationName);
        return this;
    }


    @Override
    public Object getRequest() {
        if (rpcInnerContext != null) return rpcInnerContext.getRequest();
        return null;
    }

    @Override
    public void setRequest(Object request) {
        if (rpcInnerContext != null) rpcInnerContext.setRequest(request);
    }

    @Override
    public <T> T getRequest(Class<T> clazz) {
        if (rpcInnerContext != null) return rpcInnerContext.getRequest(clazz);
        return null;
    }

    @Override
    public Object getResponse() {
        if (rpcInnerContext != null) return rpcInnerContext.getResponse();
        return null;
    }

    @Override
    public void setResponse(Object response) {
        if (rpcInnerContext != null) rpcInnerContext.setResponse(response);
        return;

    }

    @Override
    public <T> T getResponse(Class<T> clazz) {
        if (rpcInnerContext != null) return rpcInnerContext.getResponse(clazz);
        return null;
    }

    @Override
    public boolean isProviderSide() {
        if (rpcInnerContext != null) return rpcInnerContext.isProviderSide();
        return true;
    }

    @Override
    public boolean isConsumerSide() {
        if (rpcInnerContext != null) return rpcInnerContext.isConsumerSide();
        return true;
    }

    @Override
    public <T> CompletableFuture<T> getCompletableFuture() {
        if (rpcInnerContext != null) return rpcInnerContext.getCompletableFuture();
        return null;
    }

    @Override
    public List<URL> getUrls() {
        if (rpcInnerContext != null) return rpcInnerContext.getUrls();
        return null;
    }

    @Override
    public void setUrls(List<URL> urls) {
        if (rpcInnerContext != null) rpcInnerContext.setUrls(urls);
    }


    @Override
    public URL getUrl() {
        if (rpcInnerContext != null) return rpcInnerContext.getUrl();
        return null;
    }

    @Override
    public void setUrl(URL url) {
        if (rpcInnerContext != null) rpcInnerContext.setUrl(url);
    }

    @Override
    public String getMethodName() {
        if (rpcInnerContext != null) return rpcInnerContext.getMethodName();
        return null;
    }

    @Override
    public void setMethodName(String methodName) {
        if (rpcInnerContext != null) rpcInnerContext.setMethodName(methodName);
    }

    @Override
    public Class<?>[] getParameterTypes() {
        if (rpcInnerContext != null) return rpcInnerContext.getParameterTypes();
        return null;
    }

    @Override
    public void setParameterTypes(Class<?>[] parameterTypes) {
        if (rpcInnerContext != null) rpcInnerContext.setParameterTypes(parameterTypes);
    }

    @Override
    public Object[] getArguments() {
        if (rpcInnerContext != null) return rpcInnerContext.getArguments();
        return null;
    }

    @Override
    public void setArguments(Object[] arguments) {
        if (rpcInnerContext != null) rpcInnerContext.setArguments(arguments);
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        if (rpcInnerContext != null) return rpcInnerContext.getLocalAddress();
        return null;
    }

    @Override
    public String getLocalAddressString() {
        if (rpcInnerContext != null) return rpcInnerContext.getLocalAddressString();
        return null;
    }

    @Override
    public String getLocalHostName() {
        if (rpcInnerContext != null) return rpcInnerContext.getLocalHostName();
        return null;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        if (rpcInnerContext != null) return rpcInnerContext.getRemoteAddress();
        return null;
    }

    @Override
    public String getRemoteApplicationName() {
        if (rpcInnerContext != null) return rpcInnerContext.getRemoteApplicationName();
        return null;
    }

    @Override
    public String getRemoteAddressString() {
        if (rpcInnerContext != null) return rpcInnerContext.getRemoteAddressString();
        return null;
    }

    @Override
    public String getRemoteHostName() {
        if (rpcInnerContext != null) return rpcInnerContext.getRemoteHostName();
        return null;
    }

    @Override
    public String getLocalHost() {
        if (rpcInnerContext != null) return rpcInnerContext.getLocalHost();
        return null;
    }

    @Override
    public int getLocalPort() {
        if (rpcInnerContext != null) return rpcInnerContext.getLocalPort();
        return 0;
    }

    @Override
    public String getRemoteHost() {
        if (rpcInnerContext != null) return rpcInnerContext.getRemoteHost();
        return null;
    }

    @Override
    public int getRemotePort() {
        if (rpcInnerContext != null) return rpcInnerContext.getRemotePort();
        return 0;
    }

    @Override
    public String getAttachment(String key) {
        if (rpcInnerContext != null) return rpcInnerContext.getAttachment(key);
        return null;
    }

    @Override
    public Map<String, String> getAttachments() {
        if (rpcInnerContext != null) return rpcInnerContext.getAttachments();
        return null;
    }

    @Override
    public void clearAttachments() {
        if (rpcInnerContext != null) rpcInnerContext.clearAttachments();
    }

    @Override
    public Map<String, Object> get() {
        if (rpcInnerContext != null) return rpcInnerContext.get();
        return null;
    }

    @Override
    public Object get(String key) {
        if (rpcInnerContext != null) return rpcInnerContext.get(key);
        return null;
    }

    @Override
    public boolean isServerSide() {
        if (rpcInnerContext != null) return rpcInnerContext.isServerSide();
        return true;
    }

    @Override
    public boolean isClientSide() {
        if (rpcInnerContext != null) return rpcInnerContext.isClientSide();
        return true;
    }

    @Override
    public List<Invoker<?>> getInvokers() {
        if (rpcInnerContext != null) return rpcInnerContext.getInvokers();
        return null;
    }

    @Override
    public Invoker<?> getInvoker() {
        if (rpcInnerContext != null) return rpcInnerContext.getInvoker();
        return null;
    }

    @Override
    public Invocation getInvocation() {
        if (rpcInnerContext != null) return rpcInnerContext.getInvocation();
        return null;
    }

    @Override
    public <T> CompletableFuture<T> asyncCall(Callable<T> callable) {
        if (rpcInnerContext != null) return rpcInnerContext.asyncCall(callable);
        return null;
    }

    @Override
    public void asyncCall(Runnable runnable) {
        if (rpcInnerContext != null) rpcInnerContext.asyncCall(runnable);
    }

    @Override
    public boolean isAsyncStarted() {
        if (rpcInnerContext != null) return rpcInnerContext.isAsyncStarted();
        return false;
    }

    @Override
    public boolean stopAsync() {
        if (rpcInnerContext != null) return rpcInnerContext.stopAsync();
        return true;
    }

    @Override
    public AsyncContext getAsyncContext() {
        if (rpcInnerContext != null) return rpcInnerContext.getAsyncContext();
        return null;
    }


}
