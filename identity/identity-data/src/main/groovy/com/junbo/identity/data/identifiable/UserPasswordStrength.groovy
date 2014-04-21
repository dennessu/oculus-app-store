/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.identifiable

import com.junbo.common.util.Identifiable

import javax.ws.rs.NotSupportedException

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

    @Override
    void setId(Short id) {
        throw new NotSupportedException('enum UserPasswordStrength not settable')
    }
}