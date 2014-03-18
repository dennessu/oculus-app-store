/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.model

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileStatic

/**
 * Consent.
 */
@CompileStatic
class Consent {
    Long userId
    String clientId
    Set<String> scopes

    @JsonIgnore
    String revision
}
