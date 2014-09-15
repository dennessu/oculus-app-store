/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.junbo.common.cloudant.json.annotations.CloudantIgnore
import com.junbo.common.model.ResourceMeta
import groovy.transform.CompileStatic

/**
 * EmailVerifyCode.
 */
@CompileStatic
class EmailVerifyCode extends ResourceMeta<String> {
    @CloudantIgnore
    String code

    @JsonIgnore
    String encryptedCode

    @JsonIgnore
    String hashedCode
    String email
    Long userId
    Long targetMailId

    @Override
    String getId() {
        return hashedCode
    }

    @Override
    void setId(String id) {
        this.hashedCode = id
    }
}
