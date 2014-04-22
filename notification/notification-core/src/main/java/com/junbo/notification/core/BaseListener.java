/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.notification.core;

import javax.jms.*;
import java.util.UUID;

/**
 * Created by xmchen on 14-1-21.
 */
public abstract class BaseListener implements MessageListener {

    public BaseListener() {

    }

    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            try {
                onTextMessage(UUID.randomUUID().toString(), ((TextMessage) message).getText());
            } catch (JMSException ex) {
                throw new NotificationException(ex);
            }
        } else  {
            throw new NotificationException("Message should be a TextMessage");
        }
    }

    protected abstract void onTextMessage(final String eventId, final String message);
}

