/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.notification.core;

/**
 * EmailListener.
 */
public class EmailListener extends BaseListener {
    @Override
    protected void onMessage(String eventId, String message) {
        System.out.println("You've got a message [" + message + "].");
    }
}
