package com.junbo.langur.core.promise;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Shenhua on 7/15/2014.
 */
public class Looper {
    private static final Logger logger = LoggerFactory.getLogger(Looper.class);

    private static final ThreadLocal<Looper> current = new ThreadLocal<Looper>() {
        @Override
        protected Looper initialValue() {
            return new Looper();
        }
    };

    public static Looper current() {
        Looper looper = current.get();

        logger.info("Looper.current(): " + looper);
        return looper;
    }


    private final ReentrantLock lock;

    private final Condition notEmpty;

    private int startId;

    private Runnable runnable;

    public Looper() {
        lock = new ReentrantLock();
        notEmpty = lock.newCondition();
    }

    public int start() {
        lock.lock();
        try {
            startId++;

            logger.warn("Looper start: " + startId);
            return startId;
        } finally {
            lock.unlock();
        }

    }

    public void stop(final int startId) {
        lock.lock();
        try {
            if (this.startId != startId) {
                throw new IllegalStateException("this.startId " + this.startId + " != startId " + startId);
            }

            this.startId--;
            notEmpty.signal();

            logger.warn("Looper stop: " + this.startId);
        } finally {
            lock.unlock();
        }

    }

    public boolean offerRunnable(Runnable runnable) {
        lock.lock();
        try {
            if (this.runnable != null) {
                throw new IllegalStateException("this.runnable != null");
            }

            this.runnable = runnable;
            notEmpty.signal();

            if (startId == 0) {
                logger.warn("Looper offerRunnable: " + this.startId + ". false");
                return false;
            }

            logger.warn("Looper offerRunnable: " + this.startId + ". true");
            return true;
        } finally {
            lock.unlock();
        }

    }

    private Runnable poolRunnable(final int startId) {
        Runnable runnable = null;

        lock.lock();
        try {

            logger.warn("Looper poolRunnable: " + startId + ". this.startId: " + this.startId);

            while (this.startId == startId && this.runnable == null) {
                notEmpty.awaitUninterruptibly();
            }

            if (this.startId > startId) {
                throw new IllegalStateException("this.startId " + this.startId + " > startId " + startId);
            }

            if (this.runnable != null) {
                runnable = this.runnable;
                this.runnable = null;
            }
        } finally {
            lock.unlock();
        }

        return runnable;
    }

    public void run(int startId) {
        while (true) {
            Runnable runnable = poolRunnable(startId);

            if (runnable == null) {
                break;
            }

            logger.info("found one runnable: " + runnable);
            runnable.run();
        }
    }

    public static void tryToWait(ListenableFuture future) {
        if (!future.isDone()) {
            final Looper looper = Looper.current();
            final int startId = looper.start();
            future.addListener(new Runnable() {
                @Override
                public void run() {
                    looper.stop(startId);
                }
            }, MoreExecutors.sameThreadExecutor());

            looper.run(startId);
        }
    }

    public static boolean tryToSchedule(Runnable command) {
        if (command instanceof RunnableWrapper) {
            RunnableWrapper wrapper = (RunnableWrapper) command;

            Object handle = wrapper.begin();
            try {
                return Looper.current().offerRunnable(wrapper.getRunnable());
            } finally {
                wrapper.end(handle);
            }
        }

        return false;
    }
}
