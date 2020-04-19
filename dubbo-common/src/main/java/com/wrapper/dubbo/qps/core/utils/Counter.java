package com.wrapper.dubbo.qps.core.utils;

import java.io.Serializable;

public class Counter implements Serializable {
    private static final long serialVersionUID = 8537573401763461929L;
    private transient RollingNumber rollingNumber;

    public Counter(Time time, int sampleTimeInMilliseconds, int sampleNumberOfBuckets) {
        this.rollingNumber = new RollingNumber(time, (long)sampleTimeInMilliseconds, sampleNumberOfBuckets);
    }

    public Counter(int sampleTimeSeconds, int sampleNumberOfBuckets) {
        this.rollingNumber = new RollingNumber((long)(sampleTimeSeconds * 1000), sampleNumberOfBuckets);
    }

    public void incrementSlowCount() {
        this.rollingNumber.increment(RollingNumberEvent.SLOW_COUNT);
    }

    public long slowCount() {
        return this.rollingNumber.getRollingSum(RollingNumberEvent.SLOW_COUNT);
    }

    public void incrementCount() {
        this.rollingNumber.increment(RollingNumberEvent.COUNT);
    }

    public long count() {
        return this.rollingNumber.getRollingSum(RollingNumberEvent.COUNT);
    }

    public void incrementFail() {
        this.rollingNumber.increment(RollingNumberEvent.FAILURE);
    }

    public long fail() {
        return this.rollingNumber.getRollingSum(RollingNumberEvent.FAILURE);
    }

    public void incrementBizFail() {
        this.rollingNumber.increment(RollingNumberEvent.BIZ_FAILURE);
    }

    public long bizFail() {
        return this.rollingNumber.getRollingSum(RollingNumberEvent.BIZ_FAILURE);
    }

    public void incrementTimeout() {
        this.rollingNumber.increment(RollingNumberEvent.TIMEOUT);
    }

    public long timeout() {
        return this.rollingNumber.getRollingSum(RollingNumberEvent.TIMEOUT);
    }

    public void increaseRT(long timeMs) {
        this.rollingNumber.add(RollingNumberEvent.RT, timeMs);
    }

    public void increaseBizRT(long timeMs) {
        this.rollingNumber.add(RollingNumberEvent.BIZ_RT, timeMs);
    }

    public long getLastSampleCount() {
        return this.rollingNumber.getValueOfLatestBucket(RollingNumberEvent.COUNT);
    }

    public long avgRT() {
        long count = this.rollingNumber.getRollingSum(RollingNumberEvent.COUNT);
        long totalTime = this.rollingNumber.getRollingSum(RollingNumberEvent.RT);
        return count > 0L ? totalTime / count : totalTime;
    }

    public long avgBizRT() {
        long count = this.rollingNumber.getRollingSum(RollingNumberEvent.COUNT);
        long totalTime = this.rollingNumber.getRollingSum(RollingNumberEvent.BIZ_RT);
        return count > 0L ? totalTime / count : totalTime;
    }
}

