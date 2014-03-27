/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.notification.topic;

import junit.framework.Assert;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * TestSubscriber1.
 */
public class TestSubscriber2 implements MessageListener {
    @Override
    public void onMessage(Message message) {
        try {
            TextMessage msg = (TextMessage) message;
            Assert.assertNotNull(msg.getText());

            System.out.println("TestSubscriber2: " + msg.getText());
        } catch (Exception e) {
            //ignore
        }
    }
}
