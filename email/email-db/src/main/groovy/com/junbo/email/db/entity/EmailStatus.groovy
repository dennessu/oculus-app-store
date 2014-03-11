/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.entity

import com.junbo.common.util.Identifiable

/**
 * Enum of EmailStatus
 */
enum EmailStatus implements Identifiable<Short> {
    PENDING((Short)1),
    SUCCEED((Short)2),
    FAILED((Short)3),
    SCHEDULED((Short)4)

    EmailStatus(Short id) {
        this.id = id
    }

    private final Short id

    Short getId() {
        return id
    }
}