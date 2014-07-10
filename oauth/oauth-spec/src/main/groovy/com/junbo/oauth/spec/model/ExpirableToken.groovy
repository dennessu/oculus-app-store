/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.junbo.common.model.ResourceMeta
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
abstract class ExpirableToken extends ResourceMeta<String> {
    Date expiredBy

    @JsonIgnore
    boolean isExpired() {
        return expiredBy.before(new Date())
    }
}
