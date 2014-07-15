/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.common.async;

import com.google.common.util.concurrent.*;
import com.junbo.common.util.PromiseFacade;
import com.junbo.langur.core.promise.ExecutorContext;
import com.junbo.langur.core.promise.Promise;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AsyncTest.
 */
public class AsyncTest {
    private static ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(10);
    private static ListeningExecutorService LISTENING_EXECUTOR_SERVICE
            = MoreExecutors.listeningDecorator(EXECUTOR_SERVICE);

    @BeforeTest
    @SuppressWarnings("deprecation")
    public void setup() {
        ExecutorContext.setAsyncMode(true);
    }

    @AfterTest
    @SuppressWarnings("deprecation")
    public void cleanup() {
        ExecutorContext.resetAsyncMode();
    }

    @Test(enabled = false)
    public void testBVT() throws Exception {
        ListenableFuture listenableFuture = LISTENING_EXECUTOR_SERVICE.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return new Calculator().calculate(1, 2, 2000);
            }
        });

        System.out.println("after calculating call...");

        Futures.addCallback(listenableFuture, new FutureCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                System.out.println("calculation result: " + result);
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("oops");
            }
        });

        System.out.println("finished");
        listenableFuture.get();
    }

    @Test
    public void testFulfilmentSDK1() throws Exception {
        logThreadInfo();
        MyContext.current().setName("sharkmao");
        System.out.println(MyContext.current().getName());

        PromiseFacade.FULFILMENT.decorate(new Runnable() {
            public void run() {
                logThreadInfo();
                FulfilmentSDK.ship("test shipping info", 1000);

                Assert.assertEquals(MyContext.current().getName(), "sharkmao");
                MyContext.current().setAge(1000);
            }
        }).then(new Promise.Func<Void, Promise<Void>>() {
            public Promise<Void> apply(Void aVoid) {
                System.out.println("then1...");
                logThreadInfo();

                Assert.assertEquals(MyContext.current().getName(), "sharkmao");
                Assert.assertEquals(MyContext.current().getAge(), new Integer(1000));
                MyContext.current().setSalary(123456789L);

                return Promise.pure(null);
            }
        }).then(new Promise.Func<Void, Promise<Void>>() {
            public Promise<Void> apply(Void aVoid) {
                System.out.println("then2...");
                logThreadInfo();

                Assert.assertEquals(MyContext.current().getName(), "sharkmao");
                Assert.assertEquals(MyContext.current().getAge(), new Integer(1000));
                Assert.assertEquals(MyContext.current().getSalary(), new Long(123456789L));

                return Promise.pure(null);
            }
        }).testGet();
    }

    @Test
    public void testFulfilmentSDK2() throws Exception {
        logThreadInfo();
        MyContext.current().setName("sharkmao");

        PromiseFacade.FULFILMENT.decorate(new Callable<Long>() {
            public Long call() {
                logThreadInfo();

                Assert.assertEquals(MyContext.current().getName(), "sharkmao");
                MyContext.current().setAge(1000);

                return FulfilmentSDK.query("test shipping info", 1000);
            }
        }).then(new Promise.Func<Long, Promise<String>>() {
            public Promise<String> apply(Long input) {
                System.out.println("then1..." + input);
                logThreadInfo();

                Assert.assertEquals(MyContext.current().getName(), "sharkmao");
                Assert.assertEquals(MyContext.current().getAge(), new Integer(1000));
                MyContext.current().setSalary(123456789L);

                return Promise.pure("hello:" + input);
            }
        }).then(new Promise.Func<String, Promise<Void>>() {
            public Promise<Void> apply(String input) {
                System.out.println("then2..." + input);
                logThreadInfo();

                Assert.assertEquals(MyContext.current().getName(), "sharkmao");
                Assert.assertEquals(MyContext.current().getAge(), new Integer(1000));
                Assert.assertEquals(MyContext.current().getSalary(), new Long(123456789L));

                return Promise.pure(null);
            }
        }).testGet();
    }

    private void logThreadInfo() {
        System.out.println(Thread.currentThread().getName());
    }
}
