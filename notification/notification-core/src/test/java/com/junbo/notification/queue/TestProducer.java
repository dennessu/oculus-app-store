/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.notification.queue;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * TestProducer.
 */
public class TestProducer {
    private JmsTemplate template;
    private Destination destination;

    public void setTemplate(JmsTemplate template) {
        this.template = template;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public void send(final String content) {
        template.send(destination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(content);
            }
        });
    }
}
