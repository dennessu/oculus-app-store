/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.publisher;

//import com.junbo.notification.core.TransactionalPublisher;

import java.util.UUID;

/**
 * Created by xmchen on 14-4-21.
 */
public class AsyncChargePublisher {//extends TransactionalPublisher {
    public void publish(final String balanceId) {
        final String eventId = UUID.randomUUID().toString();
        //publishText(eventId, balanceId);
    }
}

