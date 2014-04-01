/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.notification.core;

import org.springframework.jms.core.JmsTemplate;

import javax.jms.Destination;

/**
 * BasePublisher.
 */
public abstract class BasePublisher {
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
}
