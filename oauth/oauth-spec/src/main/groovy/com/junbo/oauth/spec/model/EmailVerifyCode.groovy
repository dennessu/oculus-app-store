/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.model

import com.junbo.common.model.ResourceMeta
import groovy.transform.CompileStatic

/**
 * EmailVerifyCode.
 */
@CompileStatic
class EmailVerifyCode extends ResourceMeta<String> {
    String code
    String email
    Long userId

    @Override
    String getId() {
        return code
    }

    @Override
    void setId(String id) {
        this.code = id
    }
}
