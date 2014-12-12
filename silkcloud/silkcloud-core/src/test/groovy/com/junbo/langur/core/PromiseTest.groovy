package com.junbo.langur.core
import com.google.common.util.concurrent.ListeningExecutorService
import com.google.common.util.concurrent.MoreExecutors
import com.junbo.langur.core.promise.Promise
import org.testng.Assert
import org.testng.annotations.Test

import java.util.concurrent.Callable
import java.util.concurrent.Executors
/**
 * The PromiseTest class.
 */
class PromiseTest {

    ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

    boolean reentryFlag = false
    boolean flag = false

    private void checkNotReentry() {
        if (flag) {
            reentryFlag = true
        }
        flag = true
        Thread.sleep(40)
        flag = false
    }

    @Test
    public void testParallel() {
        boolean finished = false
        int iterateTime = 200
        List dummy = [] as List
        for (int i = 0; i < iterateTime; ++i) {
            dummy << i
        }

        Closure<Promise> closure = {
            callApi().then {
                checkNotReentry()
                return Promise.pure()
            }
        }

        Promise.pure().then {
            Promise.each(dummy) {
                Promise.parallel(
                        closure, closure, closure, closure, closure
                )
            }.then {
                finished = true
                return Promise.pure()
            }
        }

        while (!finished) {
            Thread.sleep(50)
        }

        Assert.assertFalse(reentryFlag)
    }

    private Promise<String> callApi() {
        return Promise.wrap(service.submit(new Callable<String>() {
            public String call() {
                Thread.sleep(50)
                return "aaa"
            }
        }))
    }



}
