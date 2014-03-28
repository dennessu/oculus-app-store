/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.promise;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * PromiseShellTest.
 */
public class PromiseShellTest {
    @Test(expectedExceptions = ExecutionException.class)
    public void testFailure() throws Exception {
        PromiseShell.PAYMENT.decorate(new Callable<Long>() {
            public Long call() throws Exception {
                return BrainTreeSDK.charge(-10, "USD");
            }
        }).recover(new Promise.Func<Throwable, Promise<Long>>() {
            public Promise<Long> apply(Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }).then(new Promise.Func<Long, Promise<String>>() {
            public Promise<String> apply(Long chargeResult) {
                return Promise.pure("hello:" + chargeResult);
            }
        }).wrapped().get();
    }

    @Test
    public void testAccuracy() throws Exception {
        PromiseShell.PAYMENT.decorate(new Callable<Long>() {
            public Long call() throws Exception {
                return BrainTreeSDK.charge(9.99, "USD");
            }
        }).recover(new Promise.Func<Throwable, Promise<Long>>() {
            public Promise<Long> apply(Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }).then(new Promise.Func<Long, Promise<String>>() {
            public Promise<String> apply(Long chargeResult) {
                Assert.assertEquals(chargeResult, new Long(123L), "charge result should match.");
                return Promise.pure("hello:" + chargeResult);
            }
        }).wrapped().get();
    }
}
