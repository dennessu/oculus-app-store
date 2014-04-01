/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.notification.core;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.UUID;

/**
 * EmailPublisher.
 */
public class EmailPublisher extends TransactionalPublisher {
    public void send(final String content) {
        final String eventId = UUID.randomUUID().toString();

        publish(new Event() {
            @Override
            public String getId() {
                return eventId;
            }

            @Override
            public Message createMessage(Session session) throws JMSException {
                System.out.println("sending message: " + content);

                return session.createTextMessage(content);
            }
        });
    }
}
