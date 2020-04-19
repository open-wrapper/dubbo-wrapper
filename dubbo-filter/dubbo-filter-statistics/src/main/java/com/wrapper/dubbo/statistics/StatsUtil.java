package com.wrapper.dubbo.statistics;

import com.wrapper.dubbo.common.utils.DubboLogger;
import com.wrapper.dubbo.qps.core.utils.Counter;
import org.apache.dubbo.common.utils.NamedThreadFactory;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.*;

import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER_SIDE;

public class StatsUtil {

    private static final Logger statisticLogger = StatisticsLogger.getLogger();
    private static final Logger logger = DubboLogger.getLogger();

    public static final int    DEFAULT_SLOW_COST                   = 50;

    private static final int sampleTimeInSeconds = 30, sampleBuckets = 10;

    private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("dubbo-stats", true));
    private static ConcurrentMap<String, Counter> statisticsMap = new ConcurrentHashMap<String, Counter>();

    static {
        executorService.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                try {
                    logAccessStatistic();
                } catch (Throwable throwable) {
                    logger.error("statis throwable.", throwable);
                }
            }
        }, sampleTimeInSeconds, sampleTimeInSeconds, TimeUnit.SECONDS);
    }

    public static void statistic(String side, String application, String interfaceName, long time, long bizTime, boolean hasException) {
        final String traceName = side + "|" + application + "|" + interfaceName;
        try {
            Counter item = statisticsMap.get(traceName);
            if (item == null) {
                statisticsMap.putIfAbsent(traceName, new Counter(sampleTimeInSeconds, sampleBuckets));
                item = statisticsMap.get(traceName);
            }
            statistic(item, time, bizTime, hasException);
        } catch (Throwable e) {
            logger.error("access statistic throwable.", e);
        }
    }

    private static void statistic(Counter counter, long costTimeMillis, long bizProcessTime, boolean hasException) {
        counter.incrementCount();
        counter.increaseRT(costTimeMillis);
        counter.increaseBizRT(bizProcessTime);

        if (costTimeMillis > DEFAULT_SLOW_COST) {
            counter.incrementSlowCount();
        }
        if (hasException) {
            counter.incrementFail();
        }
    }


    /**
     * 获取所有provider最近的调用量总数
     *
     * @return
     */
    public static Map<String, Long> getProvidersLatestCount() {
        Map<String, Long> totalResults = new ConcurrentHashMap<String, Long>();
        for (Map.Entry<String, Counter> entry : statisticsMap.entrySet()) {
            final String key = entry.getKey();
            if (key.startsWith(PROVIDER_SIDE)) {
                Counter counter = entry.getValue();
                totalResults.put(key, counter.getLastSampleCount());
            }
        }
        return totalResults;

    }


    private static void logAccessStatistic() {
        for (Map.Entry<String, Counter> entry : statisticsMap.entrySet()) {
            Counter item = entry.getValue();

            if (item.count() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(entry.getKey()).append("|")
                        .append(item.count()).append("|")
                        .append(item.slowCount()).append("|")
                        .append(item.fail()).append("|")
                        .append(item.avgRT()).append("|")
                        .append(item.avgBizRT()).append("|")
                        .append(item.count() / sampleTimeInSeconds);
                statisticLogger.info(stringBuilder.toString());
            }
        }
    }
}

