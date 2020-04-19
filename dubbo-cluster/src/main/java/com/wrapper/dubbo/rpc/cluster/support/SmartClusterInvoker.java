package com.wrapper.dubbo.rpc.cluster.support;

import com.wrapper.dubbo.rpc.cluster.feedback.Feedback;
import com.wrapper.dubbo.rpc.cluster.feedback.NoopFeedback;
import java.util.HashMap;

import com.wrapper.dubbo.common.CommonConstants;
import org.apache.dubbo.common.Version;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.common.utils.NetUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.Directory;
import org.apache.dubbo.rpc.cluster.LoadBalance;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.dubbo.rpc.cluster.support.AbstractClusterInvoker;

import static org.apache.dubbo.rpc.cluster.Constants.RETRIES_KEY;

public class SmartClusterInvoker<T> extends AbstractClusterInvoker<T> {

    public static final String REGION = "region";

    public static final int DEFAULT_SMART_RETRIES = 0;

    public static final String REGION_NAME = System.getProperty("region.name", "");


    private static final Logger logger = LoggerFactory.getLogger(SmartClusterInvoker.class);

    private static final Feedback feedback = new NoopFeedback();

    public SmartClusterInvoker(Directory<T> directory) {
        super(directory);
    }

    /**
     * @param invocation
     * @param invokers
     * @param loadbalance
     * @return
     * @throws RpcException
     */
    public Result doInvoke(Invocation invocation, final List<Invoker<T>> invokers,
                           LoadBalance loadbalance)
            throws RpcException {

        List<Invoker<T>> copyinvokers = invokers;
        checkInvokers(copyinvokers, invocation);

        String serviceName = getUrl().getServiceKey();

        // 默认不重试
        int len = getUrl().getMethodParameter(invocation.getMethodName(), RETRIES_KEY, DEFAULT_SMART_RETRIES) + 1;
        if (len <= 0) {
            len = 1;
        }

        // retry loop.
        int lastRetry = len - 1;
        RpcException le = null; // last exception.
        List<Invoker<T>> invoked = new ArrayList<Invoker<T>>(); // invoked invokers.
        Set<String> providers = new HashSet<String>(len);
        Result result = null;

        // 重试次数的最后一次，如果远程开关打开的情况下，调度远程
        outer:
        for (int i = 0; i < len; i++) {
            //重试时，进行重新选择，避免重试时invoker列表已发生变化.
            //注意：如果列表发生了变化，那么invoked判断会失效，因为invoker实例已经改变
            if (i > 0) {
                checkWhetherDestroyed();
                copyinvokers = list(invocation);
                checkInvokers(copyinvokers, invocation);
            }

            Map<String, List<Invoker<T>>> invokerGroup = getInvokerGroup(copyinvokers);
            List<Invoker<T>> localInvokers = invokerGroup.get(CommonConstants.LOCAL);
            List<Invoker<T>> remoteInvokers = invokerGroup.get(CommonConstants.REMOTE);

            boolean isRemote = false;
            // 本机房优先
            copyinvokers = localInvokers;
            if (CollectionUtils.isEmpty(localInvokers)) {
                isRemote = remoteAccess(remoteInvokers, serviceName);
                // 远程服务不可用抛出异常
                checkRemoteStatus(invocation, isRemote);
            }

            Invoker<T> invoker = null;

            if (!isRemote) {
                for (int index = 0; index < copyinvokers.size(); index++) {

                    if (invoked.containsAll(copyinvokers)) {
                        if (remoteAccess(remoteInvokers, serviceName)) {
                            isRemote = true;
                            break;
                        } else {
                            break outer;
                        }
                    } else {
                        if (lastRetry != 0 && lastRetry == i && remoteAccess(remoteInvokers, serviceName)) {
                            isRemote = true;
                            break;
                        }
                    }

                    invoker = select(loadbalance, invocation, copyinvokers, invoked);

                    if (invoked.contains(invoker)) {
                        continue;
                    } else {
                        invoked.add(invoker);
                    }
                    if (feedback.isAvailable(invoker)) {
                        break;
                    }
                }
            }

            if (isRemote) {
                copyinvokers = remoteInvokers;

                for (int index = 0; index < copyinvokers.size(); index++) {
                    if (invoked.containsAll(copyinvokers)) {
                        break outer;
                    }

                    invoker = select(loadbalance, invocation, copyinvokers, invoked);

                    if (invoked.contains(invoker)) {
                        continue;
                    } else {
                        invoked.add(invoker);
                    }

                    if (feedback.isAvailable(invoker)) {
                        break;
                    }
                }
            }

            RpcContext.getContext().setInvokers((List) invoked);
            try {
                result = invoker.invoke(invocation);
                if (le != null && logger.isWarnEnabled()) {
                    logger.warn("Although retry the method " + invocation.getMethodName() + " in the service " + getInterface().getName()
                            + " was successful by the provider " + invoker.getUrl().getAddress() + ", but there have been failed providers " + providers + " ("
                            + providers.size() + "/" + copyinvokers.size() + ") from the registry " + directory.getUrl().getAddress() + " on the consumer "
                            + NetUtils.getLocalHost() + " using the dubbo version " + Version.getVersion() + ". Last error is: " + le.getMessage(), le);
                }
                return result;
            } catch (RpcException e) {
                if (e.isBiz()) { // biz exception.
                    throw e;
                }
                le = e;
            } catch (Throwable e) {
                le = new RpcException(e.getMessage(), e);
            } finally {
                providers.add(invoker.getUrl().getAddress());
            }
        }
        throw new RpcException(le != null ? le.getCode() : 0,
                "Failed to invoke the method " + invocation.getMethodName() + " in the service " + getInterface().getName() + ". Tried " + len
                        + " times of the providers " + providers + " (" + providers.size() + "/" + copyinvokers.size() + ") from the registry " + directory
                        .getUrl().getAddress() + " on the consumer " + NetUtils.getLocalHost() + " using the dubbo version " + Version.getVersion()
                        + ". Last error is: " + (le != null ? le.getMessage() : ""), le != null && le.getCause() != null ? le.getCause() : le);
    }

    private void checkRemoteStatus(Invocation invocation, boolean isRemote) {
        if (!isRemote) {
            throw new RpcException(
                    "Failed to invoke the method " + invocation.getMethodName() + " in the service "
                            + getInterface().getName() + ". No provider available for the service " + directory
                            .getUrl().getServiceKey() + " from registry " + directory.getUrl().getAddress()
                            + " on the consumer " + NetUtils.getLocalHost() + " using the dubbo version "
                            + Version.getVersion()
                            + ". Please check if the providers have been started and registered. remote status isRemote : "
                            + isRemote);
        }
    }

    private boolean remoteAccess(List<Invoker<T>> remoteInvokers, String serviceName) {
        if (!feedback.supportFeedback(serviceName)) {
            return false;
        }
        if (CollectionUtils.isEmpty(remoteInvokers)) {
            return false;
        }

        return true;
    }

    private void put(Map<String, List<Invoker<T>>> invokerMap, Invoker invoker, String belongTo) {
        if (invokerMap.containsKey(belongTo)) {
            invokerMap.get(belongTo).add(invoker);
        } else {
            List<Invoker<T>> i = new ArrayList<>();
            i.add(invoker);
            invokerMap.put(belongTo, i);
        }
    }

    public Map<String, List<Invoker<T>>> getInvokerGroup(List<Invoker<T>> copyinvokers) {
        Map<String, List<Invoker<T>>> invokerMap = new HashMap<>();

        String localRegion = REGION_NAME;
        for (Invoker invoker : copyinvokers) {
            String region = invoker.getUrl().getParameter(REGION);

            if (StringUtils.isEmpty(localRegion)) {
                if (StringUtils.isEmpty(region)) {
                    put(invokerMap, invoker, CommonConstants.LOCAL);
                } else {
                    put(invokerMap, invoker, CommonConstants.REMOTE);
                }
            } else {
                if (StringUtils.isEmpty(region) || region.equals(localRegion)) {
                    put(invokerMap, invoker, CommonConstants.LOCAL);
                } else {
                    put(invokerMap, invoker, CommonConstants.REMOTE);
                }
            }
        }

        return invokerMap;
    }

}