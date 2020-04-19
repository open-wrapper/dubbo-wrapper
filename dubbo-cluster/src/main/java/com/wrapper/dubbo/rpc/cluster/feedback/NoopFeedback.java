package com.wrapper.dubbo.rpc.cluster.feedback;

import org.apache.dubbo.rpc.Invoker;

/**
 * @author codel
 * @since 2020-02-12
 */
public class NoopFeedback implements Feedback {

    @Override
    public boolean isAvailable(Invoker invoker) {
        return true;
    }

    @Override
    public boolean supportFeedback(String serviceName) {
        return true;
    }


}
