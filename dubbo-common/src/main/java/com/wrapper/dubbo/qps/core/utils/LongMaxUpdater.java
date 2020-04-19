package com.wrapper.dubbo.qps.core.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class LongMaxUpdater extends Striped64 implements Serializable {
    private static final long serialVersionUID = 7125708866756248015L;

    final long fn(long v, long x) {
        return v > x ? v : x;
    }

    public LongMaxUpdater() {
        this.base = -9223372036854775808L;
    }

    public void update(long x) {
        Cell[] as;
        long b;
        if ((as = this.cells) != null || (b = this.base) < x && !this.casBase(b, x)) {
            boolean uncontended = true;
            HashCode hc;
            int h = (hc = (HashCode) threadHashCode.get()).code;
            long v;
            Cell a;
            int n;
            if (as == null || (n = as.length) < 1 || (a = as[n - 1 & h]) == null || (v = a.value) < x && !(uncontended = a.cas(v, x))) {
                this.retryUpdate(x, hc, uncontended);
            }
        }

    }

    public long max() {
        Cell[] as = this.cells;
        long max = this.base;
        if (as != null) {
            int n = as.length;

            for (int i = 0; i < n; ++i) {
                Cell a = as[i];
                long v;
                if (a != null && (v = a.value) > max) {
                    max = v;
                }
            }
        }

        return max;
    }

    public void reset() {
        this.internalReset(-9223372036854775808L);
    }

    public long maxThenReset() {
        Cell[] as = this.cells;
        long max = this.base;
        this.base = -9223372036854775808L;
        if (as != null) {
            int n = as.length;

            for (int i = 0; i < n; ++i) {
                Cell a = as[i];
                if (a != null) {
                    long v = a.value;
                    a.value = -9223372036854775808L;
                    if (v > max) {
                        max = v;
                    }
                }
            }
        }

        return max;
    }

    public String toString() {
        return Long.toString(this.max());
    }

    public long longValue() {
        return this.max();
    }

    public int intValue() {
        return (int) this.max();
    }

    public float floatValue() {
        return (float) this.max();
    }

    public double doubleValue() {
        return (double) this.max();
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeLong(this.max());
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.busy = 0;
        this.cells = null;
        this.base = s.readLong();
    }
}

