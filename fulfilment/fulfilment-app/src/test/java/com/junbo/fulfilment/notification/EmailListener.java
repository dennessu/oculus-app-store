/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.notification;

import com.junbo.notification.core.BaseListener;

/**
 * EmailListener.
 */
public final class EmailListener extends BaseListener {
    @Override
    protected void onMessage(String eventId, String message) {
        System.out.println("You've got a message [" + message + "].");
    }
}
