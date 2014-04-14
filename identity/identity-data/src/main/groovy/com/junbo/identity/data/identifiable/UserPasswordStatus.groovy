/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.identifiable

import com.junbo.common.util.Identifiable

/**
 * User Password status enum
 */
enum UserPasswordStatus implements Identifiable<Short> {
    ACTIVE((short)1),
    RETIRE((short)2)

    private final Short id

    UserPasswordStatus(Short id) {
        this.id = id
    }

    @Override
    Short getId() {
        return id
    }
}