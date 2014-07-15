/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.util.promise;

import com.junbo.common.util.PromiseFacade;
import com.junbo.langur.core.promise.Promise;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.Callable;

/**
 * PromiseShellTest.
 */
public class PromiseFacadeTest {
    @Test(expectedExceptions = RuntimeException.class)
    public void testFailure() throws Exception {
        PromiseFacade.PAYMENT.decorate(new Callable<Long>() {
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
        }).testGet();
    }

    @Test
    public void testAccuracy() throws Exception {
        PromiseFacade.PAYMENT.decorate(new Callable<Long>() {
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
        }).testGet();
    }
}
