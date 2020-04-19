package com.wrapper.dubbo.qps.core.utils;

public enum RollingNumberEvent {

    COUNT(1),
    SLOW_COUNT(1),
    FAILURE(1),
    BIZ_FAILURE(1),
    TIMEOUT(1),
    RT(1),
    BIZ_RT(1);

    private final int type;

    private RollingNumberEvent(int type) {
        this.type = type;
    }

    public boolean isCounter() {
        return this.type == 1;
    }

    public boolean isMaxUpdater() {
        return this.type == 2;
    }
}
