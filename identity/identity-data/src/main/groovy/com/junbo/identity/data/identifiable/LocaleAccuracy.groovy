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
 * Created by liangfu on 6/23/14.
 */
@CompileStatic
enum LocaleAccuracy implements Identifiable<Short> {
    HIGH((short)1),
    MEDIUM((short)2),
    LOW((short)3)

    private final Short id

    LocaleAccuracy(Short id) {
        this.id = id
    }

    @Override
    Short getId() {
        return id
    }

    @Override
    void setId(Short id) {
        throw new NotSupportedException('enum LocaleAccuracy not settable')
    }
    }
