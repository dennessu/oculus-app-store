/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.identifiable

import com.junbo.common.util.Identifiable

/**
 * User profileType enum
 */
enum UserProfileType implements Identifiable<Short> {
    PAYIN((short)1),
    PAYOUT((short)2)

    private final Short id

    UserProfileType(Short id) {
        this.id = id
    }

    @Override
    Short getId() {
        return id
    }
}