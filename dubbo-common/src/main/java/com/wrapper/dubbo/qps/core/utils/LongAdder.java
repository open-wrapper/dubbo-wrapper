package com.wrapper.dubbo.qps.core.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class LongAdder extends Striped64 implements Serializable {
    private static final long serialVersionUID = -5116646884224171133L;

    final long fn(long v, long x) {
        return v + x;
    }

    public LongAdder() {
    }

    public void add(long x) {
        Cell[] as;
        long b;
        if ((as = this.cells) != null || !this.casBase(b = this.base, b + x)) {
            boolean uncontended = true;
            HashCode hc;
            int h = (hc = (HashCode)threadHashCode.get()).code;
            long v;
            Cell a;
            int n;
            if (as == null || (n = as.length) < 1 || (a = as[n - 1 & h]) == null || !(uncontended = a.cas(v = a.value, v + x))) {
                this.retryUpdate(x, hc, uncontended);
            }
        }

    }

    public void increment() {
        this.add(1L);
    }

    public void decrement() {
        this.add(-1L);
    }

    public long sum() {
        long sum = this.base;
        Cell[] as = this.cells;
        if (as != null) {
            int n = as.length;

            for(int i = 0; i < n; ++i) {
                Cell a = as[i];
                if (a != null) {
                    sum += a.value;
                }
            }
        }

        return sum;
    }

    public void reset() {
        this.internalReset(0L);
    }

    public long sumThenReset() {
        long sum = this.base;
        Cell[] as = this.cells;
        this.base = 0L;
        if (as != null) {
            int n = as.length;

            for(int i = 0; i < n; ++i) {
                Cell a = as[i];
                if (a != null) {
                    sum += a.value;
                    a.value = 0L;
                }
            }
        }

        return sum;
    }

    public String toString() {
        return Long.toString(this.sum());
    }

    public long longValue() {
        return this.sum();
    }

    public int intValue() {
        return (int)this.sum();
    }

    public float floatValue() {
        return (float)this.sum();
    }

    public double doubleValue() {
        return (double)this.sum();
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeLong(this.sum());
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.busy = 0;
        this.cells = null;
        this.base = s.readLong();
    }
}
