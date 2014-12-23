/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.notification.core;

import com.junbo.common.filter.SequenceIdFilter;
import org.slf4j.MDC;
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
    public static final String X_REQUEST_ID = "oculus-request-id";

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

    protected void publishText(final String eventId, final String content) {
        publish(new Event() {
            public String getId() {
                return eventId;
            }

            public String requestId() {
                return MDC.get(X_REQUEST_ID);
            }

            public Message createMessage(Session session) throws JMSException {
                return wrap(session.createTextMessage(content), eventId);
            }
        });
    }

    protected void publishObject(final String eventId, final Serializable content) {
        publish(new Event() {
            public String getId() {
                return eventId;
            }

            public String requestId() {
                return MDC.get(X_REQUEST_ID);
            }

            public Message createMessage(Session session) throws JMSException {
                return wrap(session.createObjectMessage(content), eventId);
            }
        });
    }

    private static Message wrap(Message message, String eventId) {
        try {
            message.setStringProperty(Constant.EVENT_ID, eventId);

            String requestId = MDC.get(SequenceIdFilter.X_REQUEST_ID);
            message.setStringProperty(Constant.REQUEST_ID, requestId);

            return message;
        } catch (JMSException e) {
            throw new NotificationException(e);
        }
    }
}
