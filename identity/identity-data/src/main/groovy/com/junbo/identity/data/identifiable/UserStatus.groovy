/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.identifiable

import com.junbo.common.util.Identifiable
import groovy.transform.CompileStatic

import javax.ws.rs.NotSupportedException

/**
 * User status enum
 */
@CompileStatic
enum UserStatus implements Identifiable<Short> {
    ACTIVE((short)1),
    SUSPEND((short)2),
    BANNED((short)3),
    DELETED((short)4)

    private final Short id

    UserStatus(Short id) {
        this.id = id
    }

    @Override
    Short getId() {
        return id
    }

    @Override
    void setId(Short id) {
        throw new NotSupportedException('enum UserStatus not settable')
    }
}