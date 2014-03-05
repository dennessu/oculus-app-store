/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.password;

import com.junbo.common.util.Identifiable;

/**
 * Created by liangfu on 2/24/14.
 */
public enum PasswordCharacterSetType implements Identifiable<Short> {
    UPPER_ALPHA((short)0),
    LOWER_ALPHA((short)1),
    DIGITAL((short)2),
    SPECIAL_ENGLISH_CHARACTER((short)3),
    SPACE((short)4);

    private Short id;

    PasswordCharacterSetType(Short id) {
        this.id = id;
    }

    @Override
    public Short getId() {
        return id;
    }
}
