/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.notification.impl

import com.junbo.email.core.notification.EmailPublisher
import com.junbo.notification.core.TransactionalPublisher
import groovy.transform.CompileStatic

/**
 * Impl of EmailPublisher.
 */
@CompileStatic
class EmailPublisherImpl extends TransactionalPublisher implements EmailPublisher {
    void send(Long id) {
        super.publishText(UUID.randomUUID().toString(), id.toString())
    }
}
