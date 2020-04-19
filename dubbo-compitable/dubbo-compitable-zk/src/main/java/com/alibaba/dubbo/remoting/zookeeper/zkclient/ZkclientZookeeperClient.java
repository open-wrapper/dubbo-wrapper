package com.alibaba.dubbo.remoting.zookeeper.zkclient;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.remoting.zookeeper.ChildListener;
import com.alibaba.dubbo.remoting.zookeeper.StateListener;
import com.alibaba.dubbo.remoting.zookeeper.support.AbstractZookeeperClient;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;

public class ZkclientZookeeperClient extends AbstractZookeeperClient<IZkChildListener> {
    private static final int SESSION_CONNECT_INTERVAL = 10;
    private final ZkClient client;

    private volatile KeeperState state = KeeperState.SyncConnected;

    private AtomicInteger reconnectThreadCount = new AtomicInteger(0);
    private final WatchedEvent sessionExpiredEvent = new WatchedEvent(Watcher.Event.EventType.None, KeeperState.Expired, null);

    public ZkclientZookeeperClient(URL url) {
        super(url);
        client = new ZkClient(url.getBackupAddress());
        client.subscribeStateChanges(new IZkStateListener() {
            @Override
            public void handleStateChanged(KeeperState state) throws Exception {
                logger.warn("zkClient state changed. from " + ZkclientZookeeperClient.this.state + "to " + state);
                ZkclientZookeeperClient.this.state = state;
                if (state == KeeperState.Disconnected) {
                    stateChanged(StateListener.DISCONNECTED);
                } else if (state == KeeperState.SyncConnected) {
                    stateChanged(StateListener.CONNECTED);
                }
            }

            @Override
            public void handleNewSession() throws Exception {
                logger.warn("zkClient state changed. to new session");
                stateChanged(StateListener.RECONNECTED);
            }

            @Override
            public void handleSessionEstablishmentError(Throwable error) throws Exception {
                logger.warn("zkClient session establish error, shutdownTrigger:" + client.getShutdownTrigger(), error);
                new Thread("zk-reconnect-thread-" + reconnectThreadCount.incrementAndGet()) {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(SESSION_CONNECT_INTERVAL * 1000);
                        } catch (InterruptedException e) {

                        }
                        try {
                            client.process(sessionExpiredEvent);
                        } catch (Throwable e) {
                            logger.warn("zkClient process session expire error", e);
                        }
                    }
                }.start();
            }
        });
    }


    public ZkclientZookeeperClient(org.apache.dubbo.common.URL url) {
        this(new URL(url));
    }

    @Override
    public void createPersistent(String path) {
        try {
            client.createPersistent(path, true);
        } catch (ZkNodeExistsException e) {
        }
    }

    @Override
    public void createEphemeral(String path) {
        try {
            client.createEphemeral(path);
        } catch (ZkNodeExistsException e) {
        }
    }

    @Override
    public void doDelete(String path) {
        try {
            client.delete(path);
        } catch (ZkNoNodeException e) {
        }
    }

    @Override
    public List<String> doGetChildren(String path) {
        try {
            return client.getChildren(path);
        } catch (ZkNoNodeException e) {
            return null;
        }
    }

    @Override
    public boolean isConnected() {
        return state == KeeperState.SyncConnected;
    }

    @Override
    public void doClose() {
        client.close();
    }

    @Override
    public IZkChildListener createTargetChildListener(String path, final ChildListener listener) {
        return new IZkChildListener() {
            public void handleChildChange(String parentPath, List<String> currentChilds)
                    throws Exception {
                listener.childChanged(parentPath, currentChilds);
            }
        };
    }

    @Override
    public List<String> addTargetChildListener(String path, final IZkChildListener listener) {
        return client.subscribeChildChanges(path, listener);
    }

    @Override
    public void removeTargetChildListener(String path, IZkChildListener listener) {
        client.unsubscribeChildChanges(path, listener);
    }

}
