/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.clientproxy;

import groovy.lang.Closure;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by fzhang on 14-3-25.
 */
public interface TransactionHelper {

    <T>  T executeInNewTransaction(Closure<T> closure);

    <T>  T executeInTransaction(Closure<T> closure);

    /**
     * Created by fzhang on 14-3-25.
     */
    @Component("orderTransactionHelper")
    public static class TransactionHelperImpl implements TransactionHelper {
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public <T>  T executeInNewTransaction(Closure<T> closure) {
            return closure.call();
        }

        @Transactional
        public <T>  T executeInTransaction(Closure<T> closure) {
            return closure.call();
        }
    }
}
