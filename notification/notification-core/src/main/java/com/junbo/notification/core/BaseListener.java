/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.notification.core;

import javax.jms.*;

/**
 * BaseListener.
 */
public abstract class BaseListener implements MessageListener {
    public BaseListener() {

    }

    public void onMessage(Message message) {
        try {
            String eventId = message.getStringProperty(Constant.EVENT_ID);

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

