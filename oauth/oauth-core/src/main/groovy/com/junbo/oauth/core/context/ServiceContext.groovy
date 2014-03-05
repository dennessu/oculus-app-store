/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.context

import groovy.transform.CompileStatic

/**
 * ServiceContext.
 */
@CompileStatic
class ServiceContext {
    private final Map<String, Object> contextAttributes = new HashMap<>()

    void setAttribute(String key, Object attribute) {
        contextAttributes[key] = attribute
    }

    Object getAttribute(String key) {
        return contextAttributes[key]
    }


}
