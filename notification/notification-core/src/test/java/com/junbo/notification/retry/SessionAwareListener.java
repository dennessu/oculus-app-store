/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.notification.retry;

import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TransactionalListener.
 */
public class SessionAwareListener implements SessionAwareMessageListener {
    public static AtomicInteger FAILED_COUNT = new AtomicInteger(0);

    private int FAILED_THRESHOLD = 4;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        System.out.println(FAILED_COUNT.get());
        FAILED_COUNT.getAndAdd(1);

        if (FAILED_COUNT.get() < FAILED_THRESHOLD) {
            //throw new RuntimeException("Error occurred in TransactionalListener");
            session.recover();
        } else {
            System.out.println("SessionAwareListener SUCCEED!");
        }
    }
}
