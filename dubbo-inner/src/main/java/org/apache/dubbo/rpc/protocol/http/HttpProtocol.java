package org.apache.dubbo.rpc.protocol.http;

/**
 * @author codel
 * @since 2020-01-07
 */
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Exporter;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.protocol.AbstractProtocol;
/**
 * HttpProtocol
 */
public class HttpProtocol extends AbstractProtocol {

    public static final int DEFAULT_PORT = 80;

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        throw new RuntimeException("forbidden http");
    }

    @Override
    protected <T> Invoker<T> protocolBindingRefer(Class<T> type, URL url) throws RpcException {
        throw new RuntimeException("forbidden http");
    }

    @Override
    public int getDefaultPort() {
        return DEFAULT_PORT;
    }
}
