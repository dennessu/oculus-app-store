/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class UserInfo {
    String sub

    String name

    @JsonProperty('given_name')
    String givenName

    @JsonProperty('family_name')
    String familyName

    @JsonProperty('middle_name')
    String middleName

    String email
}
