package com.elfmcys.yesstevemodel.util;

import java.util.concurrent.*;

public final class YSMThreadPool {

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(Math.max(2, Runtime.getRuntime().availableProcessors()), Math.max(2, Runtime.getRuntime().availableProcessors()), 30, TimeUnit.SECONDS, new LinkedBlockingQueue(128), runnable -> {
        Thread thread = new Thread(runnable, "YSM Worker");
        thread.setPriority(5);
        thread.setDaemon(true);
        return thread;
    },
            new ThreadPoolExecutor.DiscardPolicy());

    private static final ThreadPoolExecutor SYNC_EXECUTOR = new ThreadPoolExecutor(2, 4, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), runnable -> {
        Thread thread = new Thread(runnable, "YSM Sync");
        thread.setPriority(7);
        thread.setDaemon(true);
        return thread;
    });

    public static Future<?> submit(Runnable runnable) {
        return EXECUTOR.submit(runnable);
    }

    public static <T> Future<T> submitCallable(Callable<T> callable) {
        return EXECUTOR.submit(callable);
    }

    public static int getQueueSize(){
        return EXECUTOR.getQueue().size();
    }

    public static Future<?> submitSync(Runnable runnable) {
        return SYNC_EXECUTOR.submit(runnable);
    }

    public static boolean awaitTermination(int i) {
        try {
            Thread.sleep(i);
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }
}