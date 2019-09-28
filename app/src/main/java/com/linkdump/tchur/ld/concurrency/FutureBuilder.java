package com.linkdump.tchur.ld.concurrency;

import android.widget.Toast;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class FutureBuilder<T> {


    private final Executor executor;
    private FutureTask<T> futureTask;

    private Runnable runnable;
    private long timeoutLimit;
    private TimeUnit timeUnit;


    /*
       Creates the blueprint for the executor that will call the future task
    */
    private FutureBuilder() {
        executor = runnable -> {
            new Thread(runnable).start();
        };
    }

    public static FutureBuilder factory() {
        return new FutureBuilder();
    }


    public FutureBuilder setTimeout(long timeout) {
        this.timeoutLimit = timeout;
        return this;
    }

    public FutureBuilder setTimeoutWithUnit(long timeout, TimeUnit timeUnit) {
        this.timeoutLimit = timeout;
        this.timeUnit = timeUnit;
        return this;
    }

    public FutureBuilder onCancelled(Runnable runnable) {
        this.runnable = runnable;
        return this;
    }


    public FutureBuilder onComplete(Callable<T> callable) {
        futureTask = new FutureTask<>(callable);
        return this;
    }


    public T getResult() {

        executor.execute(futureTask);
        try {
            return futureTask.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }


    }


}
