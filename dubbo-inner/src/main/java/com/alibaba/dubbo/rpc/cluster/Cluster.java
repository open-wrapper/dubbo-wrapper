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
package com.alibaba.dubbo.rpc.cluster;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.Directory;

@Deprecated
public interface Cluster extends org.apache.dubbo.rpc.cluster.Cluster {

    <T> com.alibaba.dubbo.rpc.Invoker<T> join(com.alibaba.dubbo.rpc.cluster.Directory<T> directory) throws
            com.alibaba.dubbo.rpc.RpcException;

    @Override
    default <T> Invoker<T> join(Directory<T> directory) throws RpcException {
        return join(new CompatibleDirectory(directory));
    }

    class CompatibleDirectory implements com.alibaba.dubbo.rpc.cluster.Directory {

        private final Directory directory;

        public CompatibleDirectory(Directory directory) {
            this.directory = directory;
        }


        @Override
        public URL getUrl() {
            return new URL(directory.getUrl());
        }

        @Override
        public boolean isAvailable() {
            return directory.isAvailable();
        }

        @Override
        public void destroy() {
            directory.destroy();
        }

        @Override
        public List<com.alibaba.dubbo.rpc.Invoker> list(Invocation invocation) throws com.alibaba.dubbo.rpc.RpcException {
            List<Invoker> invokers = directory.list(invocation);
            if (invokers == null) {
                return null;
            }
            return invokers.stream().map(invoker ->
                    new com.alibaba.dubbo.rpc.Invoker.CompatibleInvoker(invoker)
            ).collect(Collectors.toList());
        }

        @Override
        public Class getInterface() {
            return directory.getInterface();
        }
    }


}
