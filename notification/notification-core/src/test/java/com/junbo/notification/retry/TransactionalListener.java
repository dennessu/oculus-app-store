/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.notification.retry;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TransactionalListener.
 */
public class TransactionalListener implements MessageListener {
    public static AtomicInteger FAILED_COUNT = new AtomicInteger(0);

    private int FAILED_THRESHOLD = 5;

    @Override
    public void onMessage(Message message) {
        System.out.println(FAILED_COUNT.get());
        FAILED_COUNT.getAndAdd(1);

        if (FAILED_COUNT.get() < FAILED_THRESHOLD) {
            throw new RuntimeException("Error occurred in TransactionalListener");
        }

        System.out.println("SUCCEED!");
    }
}
