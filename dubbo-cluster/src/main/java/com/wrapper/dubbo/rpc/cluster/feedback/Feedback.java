package com.wrapper.dubbo.rpc.cluster.feedback;

import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.SPI;
import org.apache.dubbo.rpc.Invoker;

/**
 * @author codel
 * @since 2020-02-12
 */
@SPI("noop")
public interface Feedback {

    @Adaptive
    boolean isAvailable(Invoker invoker);

    @Adaptive
    boolean supportFeedback(String serviceName);
}
