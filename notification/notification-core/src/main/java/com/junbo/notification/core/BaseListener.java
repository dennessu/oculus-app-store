/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.notification.core;

import com.junbo.common.util.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.jms.*;

/**
 * BaseListener.
 */
public abstract class BaseListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);


    public BaseListener() {

    }

    public void onMessage(Message message) {

        try {
            String eventId = message.getStringProperty(Constant.EVENT_ID);

            String requestId = message.getStringProperty(Constant.REQUEST_ID);
            MDC.put(Context.X_REQUEST_ID, requestId);

            LOGGER.info("Message Received: eventId = " + eventId);

            if (message instanceof TextMessage) {
                onMessage(eventId, ((TextMessage) message).getText());
            } else if (message instanceof ObjectMessage) {
                onMessage(eventId, ((ObjectMessage) message).getObject());
            } else {
                throw new NotificationException("Unrecognized message type.");
            }
        } catch (JMSException e) {
            throw new NotificationException(e);
        }
    }

    protected void onMessage(final String eventId, final String content) {
        throw new NotificationException("Unimplemented.");
    }

    protected void onMessage(final String eventId, final Object content) {
        throw new NotificationException("Unimplemented.");
    }
}

