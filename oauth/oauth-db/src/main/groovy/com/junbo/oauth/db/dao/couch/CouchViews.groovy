/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.couch

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic

/**
 * CouchViews.
 */
@CompileStatic
@JsonInclude(JsonInclude.Include.NON_NULL)
class CouchViews {
    @JsonProperty('_id')
    String id

    @JsonProperty('_rev')
    String revision

    String language = 'javascript'

    Map<String, CouchView> views

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class CouchView {
        String map
        String reduce
        @JsonIgnore
        Class resultClass
    }
}
