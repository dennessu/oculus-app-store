/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.entity

import groovy.transform.CompileStatic

/**
 * ConsentEntity.
 */
@CompileStatic
class ConsentEntity extends BaseEntity {
    Long userId
    String clientId
    Set<String> scopes
}
