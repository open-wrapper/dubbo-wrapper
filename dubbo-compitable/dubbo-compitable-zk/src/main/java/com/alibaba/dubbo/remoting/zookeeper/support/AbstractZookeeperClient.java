package com.alibaba.dubbo.remoting.zookeeper.support;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.NamedThreadFactory;
import com.alibaba.dubbo.remoting.zookeeper.ChildListener;
import com.alibaba.dubbo.remoting.zookeeper.StateListener;
import com.alibaba.dubbo.remoting.zookeeper.ZookeeperClient;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;

public abstract class AbstractZookeeperClient<TargetChildListener> implements ZookeeperClient {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractZookeeperClient.class);

    private final URL url;

    private final Set<StateListener> stateListeners = new CopyOnWriteArraySet<StateListener>();

    private final ConcurrentMap<String, ConcurrentMap<ChildListener, TargetChildListener>> childListeners = new ConcurrentHashMap<String, ConcurrentMap<ChildListener, TargetChildListener>>();

    private volatile boolean closed = false;

    private static ScheduledExecutorService qpsExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("zk-qps-executor"));

    public AbstractZookeeperClient(URL url) {
        this.url = url;
        qpsExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                printQps();
            }

        }, 60, 60, TimeUnit.SECONDS);
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public void create(String path, boolean ephemeral) {
        int i = path.lastIndexOf('/');
        if (i > 0) {
            create(path.substring(0, i), false);
        }
        final long begin = System.currentTimeMillis();

        if (ephemeral) {
            try {
                createEphemeral(path);
            } finally {
                qps("createEphemeral", (System.currentTimeMillis() - begin));
            }
        } else {
            try {
                createPersistent(path);
            } finally {
                qps("createPersistent", (System.currentTimeMillis() - begin));
            }
        }
    }

    @Override
    public void addStateListener(StateListener listener) {
        stateListeners.add(listener);
    }

    @Override
    public void removeStateListener(StateListener listener) {
        stateListeners.remove(listener);
    }

    public Set<StateListener> getSessionListeners() {
        return stateListeners;
    }

    @Override
    public List<String> addChildListener(String path, final ChildListener listener) {
        ConcurrentMap<ChildListener, TargetChildListener> listeners = childListeners.get(path);
        if (listeners == null) {
            childListeners.putIfAbsent(path, new ConcurrentHashMap<ChildListener, TargetChildListener>());
            listeners = childListeners.get(path);
        }
        TargetChildListener targetListener = listeners.get(listener);
        if (targetListener == null) {
            listeners.putIfAbsent(listener, createTargetChildListener(path, listener));
            targetListener = listeners.get(listener);
        }
        final long begin = System.currentTimeMillis();
        try {
            return addTargetChildListener(path, targetListener);
        } finally {
            qps("addChildListener", (System.currentTimeMillis() - begin));
        }
    }

    @Override
    public void removeChildListener(String path, ChildListener listener) {
        ConcurrentMap<ChildListener, TargetChildListener> listeners = childListeners.get(path);
        if (listeners != null) {
            TargetChildListener targetListener = listeners.remove(listener);
            if (targetListener != null) {
                final long begin = System.currentTimeMillis();
                try {
                    removeTargetChildListener(path, targetListener);
                } finally {
                    qps("removeChildListener", (System.currentTimeMillis() - begin));
                }
            }
        }
    }

    @Override
    public void delete(String path) {
        final long begin = System.currentTimeMillis();
        try {
            doDelete(path);
        } finally {
            qps("delete", (System.currentTimeMillis() - begin));
        }
    }

    @Override
    public List<String> getChildren(String path) {
        final long begin = System.currentTimeMillis();
        try {
            return doGetChildren(path);
        } finally {
            qps("getChildren", (System.currentTimeMillis() - begin));
        }
    }

    protected void stateChanged(int state) {
        for (StateListener sessionListener : getSessionListeners()) {
            sessionListener.stateChanged(state);
        }
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        try {
            doClose();
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        }
        if (!qpsExecutor.isShutdown()) {
            qpsExecutor.shutdownNow();
        }
    }

    private void qps(String tag, long time) {
    }

    private void printQps() {
    }

    protected abstract void doClose();

    protected abstract void createPersistent(String path);

    protected abstract void createEphemeral(String path);

    protected abstract TargetChildListener createTargetChildListener(String path, ChildListener listener);

    protected abstract List<String> addTargetChildListener(String path, TargetChildListener listener);

    protected abstract void removeTargetChildListener(String path, TargetChildListener listener);

    protected abstract void doDelete(String path);

    protected abstract List<String> doGetChildren(String path);
}
