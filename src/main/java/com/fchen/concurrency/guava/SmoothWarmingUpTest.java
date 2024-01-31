package com.fchen.concurrency.guava;

import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.RateLimiter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.min;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author: Fchen
 * @date: 2021/9/27 6:54 下午
 * @desc: TODO
 */
public class SmoothWarmingUpTest {

    private static ScheduledExecutorService schedule = new ScheduledThreadPoolExecutor(1,
            new ThreadFactoryBuilder().setNameFormat("RaleLimit").setDaemon(true).build());


    public static void main(String[] args) {
        RateLimiter limiter = RateLimiter.create(2, 1, TimeUnit.SECONDS);

        while (true)
        {
            // 2个须要1s，但许可获取时间由下一次获取承担; 循环的首次，此行等待时间为0，第二次之后的等待时间为⑤ 行的许可获取时间，即0.5s
            System.out.println("get 2 tokens: " + limiter.acquire(2) + "s");
            try {
                // 休息1.5s，能够彻底消耗上行的2个许可，并剩出来0.5s，可是由于 预热/冷却时间为0，因此这个0.5s 实际上是彻底白白的流逝了..
                Thread.sleep(1500);
            } catch (Exception e) {
            }
            // 由于 预热/冷却时间为0，前面的许可已经彻底被补偿，因此此处3个须要1.5s，但时间地点时间为0，许可获取时间由下一次获取承担
            System.out.println("get 3 tokens: " + limiter.acquire(3) + "s");
            // 上一个方法的实际等待时间为0，由于它由此方法承担—— 此方法实际等待时间为上一个方法的3个许可获取时间，即1.5s
            System.out.println("get 1 tokens: " + limiter.acquire(1) + "s");
            // 此方法实际等待时间为上一个方法的1个许可获取时间，即0.5s
            System.out.println("get 1 tokens: " + limiter.acquire(1) + "s");
            // ⑤ 此方法实际等待时间为上一个方法的1个许可获取时间，即0.5s
            System.out.println("get 1 tokens: " + limiter.acquire(1) + "s");
            System.out.println("end");
        }

//        System.out.println("get ticket: " +  limiter.acquire(2) + "s");
//        System.out.println("get ticket: " +  limiter.acquire(20) + "s");
//        schedule.scheduleAtFixedRate(() -> {
//            System.out.println("get ticket: " +  limiter.acquire(20) + "s");
//            System.out.println("get ticket: " +  limiter.acquire(1) + "s");
//            System.out.println("get ticket: " +  limiter.acquire(1) + "s");
//            System.out.println("-------------");
//        }, 1, 1, TimeUnit.SECONDS);
//
//        try {
//            TimeUnit.SECONDS.sleep(20);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }



    /**
     *  存储的令牌
     */
    double storedPermits;

    /**
     * 最大的令牌
     */
    double maxPermits;

    /**
     * 获取令牌的频率
     */
    double stableIntervalMicros;

    /**
     * 冷却时间间隔
     */
    private long warmupPeriodMicros;

    /**
     * 斜率
     */
    private double slope;
    private double thresholdPermits;
    private double coldFactor = 3.0;


    void create(double permitsPerSecond, long warmupPeriod, TimeUnit timeUnit, double coldFactor) {
        warmupPeriodMicros = timeUnit.toMicros(warmupPeriod);
        doSetRate(permitsPerSecond);
    }

    void doSetRate(double permitsPerSecond) {
        stableIntervalMicros = SECONDS.toMicros(1L) / permitsPerSecond;
        double oldMaxPermits = maxPermits;
        // 冷却时间间隔是 初始化时间间隔 3 倍
        double coldIntervalMicros = stableIntervalMicros * coldFactor;

        thresholdPermits = 0.5 * warmupPeriodMicros / stableIntervalMicros;
        // 最大令牌数
        maxPermits = thresholdPermits + 2.0 * warmupPeriodMicros / (stableIntervalMicros + coldIntervalMicros);
        slope = (coldIntervalMicros - stableIntervalMicros) / (maxPermits - thresholdPermits);
        if (oldMaxPermits == Double.POSITIVE_INFINITY) {
            // if we don't special-case this, we would get storedPermits == NaN, below
            storedPermits = 0.0;
        } else {
            storedPermits =
                    (oldMaxPermits == 0.0)
                            ? maxPermits // initial state is cold
                            : storedPermits * maxPermits / oldMaxPermits;
        }
    }


    /**
     *               ^ throttling
     *               |
     *         cold  +                  /
     *      interval |                 /.
     *               |                / .
     *               |               /  .   ← "warmup period" is the area of the trapezoid between
     *               |              /   .     thresholdPermits and maxPermits
     *               |             /    .
     *               |            /     .
     *               |           /      .
     *        stable +----------/  WARM .
     *      interval |          .   UP  .
     *               |          . PERIOD.
     *               |          .       .
     *             0 +----------+-------+--------------→ storedPermits
     *               0 thresholdPermits maxPermits
     *
     */
    long storedPermitsToWaitTime(double storedPermits, double permitsToTake) {
        // 获取右边梯形所能提供的 令牌数
        double availablePermitsAboveThreshold = storedPermits - thresholdPermits;
        long micros = 0;
        // measuring the integral on the right part of the function (the climbing line)
        if (availablePermitsAboveThreshold > 0.0) {
            // 从右边部分获取令牌数
            double permitsAboveThresholdToTake = min(availablePermitsAboveThreshold, permitsToTake);
            // TODO(cpovirk): Figure out a good name for this variable.
            // 对应 梯形 的 下底
            double length = permitsToTime(availablePermitsAboveThreshold)
                       // 对应梯形的 上底
                    + permitsToTime(availablePermitsAboveThreshold - permitsAboveThresholdToTake);
            // 梯形的面积
            micros = (long) (permitsAboveThresholdToTake * length / 2.0);
            // 预支的令牌数
            permitsToTake -= permitsAboveThresholdToTake;
        }
        // measuring the integral on the left part of the function (the horizontal line)
        micros += (stableIntervalMicros * permitsToTake);
        return micros;
    }

    // 计算 y 的值
    private double permitsToTime(double permits) {
        return stableIntervalMicros + permits * slope;
    }

}
