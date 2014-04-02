/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.notification.core;

import java.util.UUID;

/**
 * EmailPublisher.
 */
public class EmailPublisher extends TransactionalPublisher {
    public void send(final String content) {
        final String eventId = UUID.randomUUID().toString();
        publishText(eventId, content);
    }
}
