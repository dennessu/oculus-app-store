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
 * Created by liangfu on 2/24/14.
 */
@CompileStatic
enum PasswordCharacterSetType implements Identifiable<Short> {
    UPPER_ALPHA((short)0),
    LOWER_ALPHA((short)1),
    DIGITAL((short)2),
    SPECIAL_ENGLISH_CHARACTER((short)3),
    SPACE((short)4)

    final private Short id

    PasswordCharacterSetType(Short id) {
        this.id = id
    }

    @Override
    Short getId() {
        return id
    }

    @Override
    void setId(Short id) {
        throw new NotSupportedException('enum PasswordCharacterSetType not settable')
    }
}
