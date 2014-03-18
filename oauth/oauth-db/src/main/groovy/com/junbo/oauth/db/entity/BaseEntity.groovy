/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
@JsonInclude(JsonInclude.Include.NON_NULL)
class BaseEntity {
    @JsonProperty('_id')
    String id

    @JsonProperty('_rev')
    String revision

    Date expiredBy

    String createdBy

    Date createdDate

    String updatedBy

    Date updatedDate

    @JsonProperty('_deleted')
    Boolean deleted

    @JsonIgnore
    Boolean getDeleted() {
        return deleted
    }

    Boolean isDeleted() {
        return deleted
    }
}
