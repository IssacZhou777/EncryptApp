package com.jellyjoe.encypt.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhouguodong on 6/16/16.
 */
public class MyExcutorManager {
    private static byte[] lock = new byte[0];
    private static MyExcutorManager instance = null;

    ExecutorService executorService ;
    ExecutorService  singleThreadExecutorService ;

    private MyExcutorManager() {
        executorService = Executors.newFixedThreadPool(8);
        singleThreadExecutorService = Executors.newSingleThreadExecutor();
    }

    public static MyExcutorManager getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new MyExcutorManager();
                }
                return instance;
            }
        }
        return instance;
    }

    /**
     * 线程池中线程在单线程中有序执行
     * @param runnable
     */
    public void singleThreadExecute(Runnable runnable){
        singleThreadExecutorService.execute(runnable);
    }

    /**
     * 执行runnable
     * @param runnable
     */
    public void execute(Runnable runnable){
        executorService.execute(runnable);
    }
}
