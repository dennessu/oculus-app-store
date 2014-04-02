/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.notification.core;

import org.springframework.jms.core.JmsTemplate;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.io.Serializable;

/**
 * BasePublisher.
 */
public abstract class BasePublisher {
    protected JmsTemplate template;
    protected Destination destination;

    public void setTemplate(JmsTemplate template) {
        this.template = template;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public BasePublisher() {

    }

    protected void publish(final Event event) {
        template.send(event);
    }

    protected void publishText(final String eventId, final String message) {
        publish(new Event() {
            public String getId() {
                return eventId;
            }

            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(message);
            }
        });
    }

    protected void publishObject(final String eventId, final Serializable message) {
        publish(new Event() {
            public String getId() {
                return eventId;
            }

            public Message createMessage(Session session) throws JMSException {
                return session.createObjectMessage(message);
            }
        });
    }
}
