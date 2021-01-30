package com.holddie.reactor.prectice.util;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Helpers {

    private static final Logger log = LoggerFactory.getLogger(Helpers.class);

    public static void sleepMillis(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error("Interrupted Thread");
            throw new RuntimeException("Interrupted thread");
        }
    }

    public static void wait(CountDownLatch waitOn) {
        try {
            waitOn.await();
        } catch (InterruptedException e) {
            log.error("Interrupted waiting on CountDownLatch");
            throw new RuntimeException("Interrupted thread");
        }
    }
}
