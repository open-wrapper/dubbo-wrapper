package com.wrapper.dubbo.statistics;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class StatsUtilTest {
    private Random random = new Random();

    @Test
    @Ignore
    public void test_statistic() throws Exception {
        for (int i = 0; i < 100000; i++) {
            StatsUtil.statistic("provider", "test", "test", random.nextInt(100), random.nextInt(100), false);
            Thread.sleep(random.nextInt(200) + random.nextInt(100));
        }

        new CountDownLatch(1).wait();

    }
}