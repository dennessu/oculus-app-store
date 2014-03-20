/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.clientproxy.impl.mandrill

/**
 * Status of Mandrill.
 */
enum SendStatus {
    SENT('sent'),
    QUEUED('queued'),
    INVALID('invalid'),
    REJECTED('rejected'),
    SCHEDULED('scheduled')

    final String status

    SendStatus(String status) {
        this.status = status
    }
}