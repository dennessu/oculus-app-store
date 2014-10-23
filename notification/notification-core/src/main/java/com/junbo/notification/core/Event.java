/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.notification.core;

import org.springframework.jms.core.MessageCreator;

/**
 * Event.
 */
public interface Event extends MessageCreator {
    String getId();

    String requestId();
}
