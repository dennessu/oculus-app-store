/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.user

import com.junbo.common.util.Identifiable

/**
 * User password strength. Please refer to http://en.wikipedia.org/wiki/Password_strength
 */
enum UserPasswordStrength implements Identifiable<Short> {
    WEAK((short)1),
    FAIR((short)2),
    STRONG((short)3)

    private final Short id

    UserPasswordStrength(Short val) {
        this.id = val
    }

    @Override
    Short getId() {
        return id
    }
}