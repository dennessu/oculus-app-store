/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.couch

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic

/**
 * CouchResponse.
 */
@CompileStatic
class CouchResponse {
    boolean ok

    String id

    @JsonProperty('rev')
    String revision
}
