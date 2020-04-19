/*
 * Copyright 1999-2012 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wrapper.dubbo.statistics.filter;

import com.wrapper.dubbo.statistics.StatsUtil;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import static com.wrapper.dubbo.common.CommonConstants.WD_BIZ_TIME;

/**
 * 目前只支持同步调用，为改成异步调用，符合现在的dubbo调用情况，全部是同步调用
 */
@Activate(group = {CommonConstants.CONSUMER, CommonConstants.PROVIDER})
public class StatisticsFilter implements Filter {


    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        final long start = System.currentTimeMillis();
        long bizTime = 0;
        boolean hasException = false;
        try {
            Result result = invoker.invoke(invocation);
            if (result != null) {
                bizTime = toLong(result.getAttachment(WD_BIZ_TIME));
                hasException = result.hasException();
            }
            return result;
        } finally {
            final long end = System.currentTimeMillis();

            StatsUtil.statistic(
                    invoker.getUrl().getParameter(CommonConstants.SIDE_KEY),
                    invoker.getUrl().getParameter(CommonConstants.APPLICATION_KEY),
                    getInterfaceName(invoker, invocation),
                    end - start,
                    bizTime,
                    hasException
            );
        }
    }

    private long toLong(String value) {
        try {
            return value == null ? 0 : Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String getInterfaceName(Invoker<?> invoker, Invocation invocation) {
        return invoker.getUrl().getServiceInterface() + ":" + invoker.getUrl().getParameter(CommonConstants.VERSION_KEY) + "." + invocation.getMethodName();
    }

}
