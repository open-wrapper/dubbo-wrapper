//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wrapper.dubbo.qps.core.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;

public class RollingNumber implements Serializable {
    private static final long serialVersionUID = 1437880145080150735L;
    private final long timeInMilliseconds;
    private final int numberOfBuckets;
    private final long millisecondsInBucket;
    private final RollingNumber.BucketCircularArray buckets;
    private final Time time;
    private ReentrantLock newBucketLock;

    public RollingNumber(long timeInMilliseconds, int numberOfBuckets) {
        this(() -> System.currentTimeMillis(), timeInMilliseconds, numberOfBuckets);
    }

    public RollingNumber(Time time, long timeInMilliseconds, int numberOfBuckets) {
        this.newBucketLock = new ReentrantLock();
        this.timeInMilliseconds = timeInMilliseconds;
        this.numberOfBuckets = numberOfBuckets;
        this.time = time;
        if (timeInMilliseconds % (long) numberOfBuckets != 0L) {
            throw new IllegalArgumentException("The timeInMilliseconds must divide equally into numberOfBuckets. For example 1000/10 is ok, 1000/11 is not.");
        } else {
            this.millisecondsInBucket = timeInMilliseconds / (long) numberOfBuckets;
            this.buckets = new RollingNumber.BucketCircularArray(numberOfBuckets);
        }
    }

    public void increment(RollingNumberEvent type) {
        this.getCurrentBucket().getAdder(type).increment();
    }

    public void add(RollingNumberEvent type, long value) {
        this.getCurrentBucket().getAdder(type).add(value);
    }

    public void updateRollingMax(RollingNumberEvent type, long value) {
        this.getCurrentBucket().getMaxUpdater(type).update(value);
    }

    public long getRollingSum(RollingNumberEvent type) {
        RollingNumber.Bucket lastBucket = this.getCurrentBucket();
        if (lastBucket == null) {
            return 0L;
        } else {
            long sum = 0L;

            RollingNumber.Bucket b;
            for (Iterator i$ = this.buckets.iterator(); i$.hasNext(); sum += b.getAdder(type).sum()) {
                b = (RollingNumber.Bucket) i$.next();
            }

            return sum;
        }
    }

    public long getValueOfLatestBucket(RollingNumberEvent type) {
        RollingNumber.Bucket lastBucket = this.getCurrentBucket();
        return lastBucket == null ? 0L : lastBucket.get(type);
    }

    public long[] getValues(RollingNumberEvent type) {
        RollingNumber.Bucket lastBucket = this.getCurrentBucket();
        if (lastBucket == null) {
            return new long[0];
        } else {
            RollingNumber.Bucket[] bucketArray = this.buckets.getArray();
            long[] values = new long[bucketArray.length];
            int i = 0;
            RollingNumber.Bucket[] arr$ = bucketArray;
            int len$ = bucketArray.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                RollingNumber.Bucket bucket = arr$[i$];
                if (type.isCounter()) {
                    values[i++] = bucket.getAdder(type).sum();
                } else if (type.isMaxUpdater()) {
                    values[i++] = bucket.getMaxUpdater(type).max();
                }
            }

            return values;
        }
    }

    public long getRollingMaxValue(RollingNumberEvent type) {
        long[] values = this.getValues(type);
        if (values.length == 0) {
            return 0L;
        } else {
            Arrays.sort(values);
            return values[values.length - 1];
        }
    }

    public void reset() {
        this.buckets.clear();
    }

    private RollingNumber.Bucket getCurrentBucket() {
        long currentTime = this.time.getCurrentTimeInMillis();
        RollingNumber.Bucket currentBucket = this.buckets.peekLast();
        if (currentBucket != null && currentTime < currentBucket.windowStart + this.millisecondsInBucket) {
            return currentBucket;
        } else if (!this.newBucketLock.tryLock()) {
            currentBucket = this.buckets.peekLast();
            if (currentBucket != null) {
                return currentBucket;
            } else {
                try {
                    Thread.sleep(5L);
                } catch (Exception var10) {
                }

                return this.getCurrentBucket();
            }
        } else {
            RollingNumber.Bucket lastBucket;
            try {
                RollingNumber.Bucket newBucket;
                if (this.buckets.peekLast() != null) {
                    for (int i = 0; i < this.numberOfBuckets; ++i) {
                        lastBucket = this.buckets.peekLast();
                        RollingNumber.Bucket var6;
                        if (currentTime < lastBucket.windowStart + this.millisecondsInBucket) {
                            var6 = lastBucket;
                            return var6;
                        }

                        if (currentTime - (lastBucket.windowStart + this.millisecondsInBucket) > this.timeInMilliseconds) {
                            this.reset();
                            var6 = this.getCurrentBucket();
                            return var6;
                        }

                        this.buckets.addLast(new RollingNumber.Bucket(lastBucket.windowStart + this.millisecondsInBucket));
                    }

                    newBucket = this.buckets.peekLast();
                    return newBucket;
                }

                newBucket = new RollingNumber.Bucket(currentTime);
                this.buckets.addLast(newBucket);
                lastBucket = newBucket;
            } finally {
                this.newBucketLock.unlock();
            }

            return lastBucket;
        }
    }

    private class BucketCircularArray implements Iterable<RollingNumber.Bucket>, Serializable {
        private static final long serialVersionUID = -8973969734912754681L;
        private final AtomicReference<RollingNumber.BucketCircularArray.ListState> state;
        private final int dataLength;
        private final int numBuckets;

        public BucketCircularArray(int size) {
            AtomicReferenceArray<RollingNumber.Bucket> _buckets = new AtomicReferenceArray(size + 1);
            this.state = new AtomicReference(new RollingNumber.BucketCircularArray.ListState(_buckets, 0, 0));
            this.dataLength = _buckets.length();
            this.numBuckets = size;
        }

        public Iterator<RollingNumber.Bucket> iterator() {
            return Collections.unmodifiableList(Arrays.asList(this.getArray())).iterator();
        }

        public void clear() {
            RollingNumber.BucketCircularArray.ListState current;
            RollingNumber.BucketCircularArray.ListState newState;
            do {
                current = (RollingNumber.BucketCircularArray.ListState) this.state.get();
                newState = current.clear();
            } while (!this.state.compareAndSet(current, newState));

        }

        public void addLast(RollingNumber.Bucket o) {
            RollingNumber.BucketCircularArray.ListState currentState = (RollingNumber.BucketCircularArray.ListState) this.state.get();
            RollingNumber.BucketCircularArray.ListState newState = currentState.addBucket(o);
            if (!this.state.compareAndSet(currentState, newState)) {
                ;
            }
        }

        public RollingNumber.Bucket getLast() {
            return this.peekLast();
        }

        public int size() {
            return ((RollingNumber.BucketCircularArray.ListState) this.state.get()).size;
        }

        public RollingNumber.Bucket peekLast() {
            return ((RollingNumber.BucketCircularArray.ListState) this.state.get()).tail();
        }

        private RollingNumber.Bucket[] getArray() {
            return ((RollingNumber.BucketCircularArray.ListState) this.state.get()).getArray();
        }

        private class ListState {
            private final AtomicReferenceArray<RollingNumber.Bucket> data;
            private final int size;
            private final int tail;
            private final int head;

            private ListState(AtomicReferenceArray<RollingNumber.Bucket> data, int head, int tail) {
                this.head = head;
                this.tail = tail;
                if (head == 0 && tail == 0) {
                    this.size = 0;
                } else {
                    this.size = (tail + BucketCircularArray.this.dataLength - head) % BucketCircularArray.this.dataLength;
                }

                this.data = data;
            }

            public RollingNumber.Bucket tail() {
                return this.size == 0 ? null : (RollingNumber.Bucket) this.data.get(this.convert(this.size - 1));
            }

            private RollingNumber.Bucket[] getArray() {
                ArrayList<RollingNumber.Bucket> array = new ArrayList();

                for (int i = 0; i < this.size; ++i) {
                    array.add(this.data.get(this.convert(i)));
                }

                return (RollingNumber.Bucket[]) array.toArray(new RollingNumber.Bucket[array.size()]);
            }

            private RollingNumber.BucketCircularArray.ListState incrementTail() {
                return this.size == BucketCircularArray.this.numBuckets ? BucketCircularArray.this.new ListState(this.data, (this.head + 1) % BucketCircularArray.this.dataLength, (this.tail + 1) % BucketCircularArray.this.dataLength) : BucketCircularArray.this.new ListState(this.data, this.head, (this.tail + 1) % BucketCircularArray.this.dataLength);
            }

            public RollingNumber.BucketCircularArray.ListState clear() {
                return BucketCircularArray.this.new ListState(new AtomicReferenceArray(BucketCircularArray.this.dataLength), 0, 0);
            }

            public RollingNumber.BucketCircularArray.ListState addBucket(RollingNumber.Bucket b) {
                this.data.set(this.tail, b);
                return this.incrementTail();
            }

            private int convert(int index) {
                return (index + this.head) % BucketCircularArray.this.dataLength;
            }
        }
    }

    private static class Bucket implements Serializable {
        final long windowStart;
        final LongAdder[] adderForCounterType;
        final LongMaxUpdater[] updaterForCounterType;

        Bucket(long startTime) {
            this.windowStart = startTime;
            this.adderForCounterType = new LongAdder[RollingNumberEvent.values().length];
            RollingNumberEvent[] arr$ = RollingNumberEvent.values();
            int len$ = arr$.length;

            int i$;
            RollingNumberEvent type;
            for (i$ = 0; i$ < len$; ++i$) {
                type = arr$[i$];
                if (type.isCounter()) {
                    this.adderForCounterType[type.ordinal()] = new LongAdder();
                }
            }

            this.updaterForCounterType = new LongMaxUpdater[RollingNumberEvent.values().length];
            arr$ = RollingNumberEvent.values();
            len$ = arr$.length;

            for (i$ = 0; i$ < len$; ++i$) {
                type = arr$[i$];
                if (type.isMaxUpdater()) {
                    this.updaterForCounterType[type.ordinal()] = new LongMaxUpdater();
                    this.updaterForCounterType[type.ordinal()].update(0L);
                }
            }

        }

        long get(RollingNumberEvent type) {
            if (type.isCounter()) {
                return this.adderForCounterType[type.ordinal()].sum();
            } else if (type.isMaxUpdater()) {
                return this.updaterForCounterType[type.ordinal()].max();
            } else {
                throw new IllegalStateException("Unknown type of event: " + type.name());
            }
        }

        LongAdder getAdder(RollingNumberEvent type) {
            if (!type.isCounter()) {
                throw new IllegalStateException("Type is not a Counter: " + type.name());
            } else {
                return this.adderForCounterType[type.ordinal()];
            }
        }

        LongMaxUpdater getMaxUpdater(RollingNumberEvent type) {
            if (!type.isMaxUpdater()) {
                throw new IllegalStateException("Type is not a MaxUpdater: " + type.name());
            } else {
                return this.updaterForCounterType[type.ordinal()];
            }
        }
    }
}
