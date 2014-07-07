/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.model

import com.junbo.common.model.ResourceMeta
import groovy.transform.CompileStatic

/**
 * Scope.
 */
@CompileStatic
class Scope extends ResourceMeta<String> {
    String name
    String description
    String logoUri
    String revision

    @Override
    String getId() {
        return name
    }

    @Override
    void setId(String id) {
        this.name = id
    }
}
