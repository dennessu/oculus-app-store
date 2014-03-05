/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.model

import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
enum Display {
    PAGE,
    POPUP,
    TOUCH,
    WAP

    static boolean isValid(String value) {
        try {
            valueOf(value.toUpperCase())
            return true
        } catch (IllegalArgumentException e) {
            return false
        }
    }
}
