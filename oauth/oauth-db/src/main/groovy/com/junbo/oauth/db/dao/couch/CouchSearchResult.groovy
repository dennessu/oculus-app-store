/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.couch

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic

/**
 * CouchSearchResult.
 */
@CompileStatic
class CouchSearchResult<T, V> {
    @JsonProperty('total_rows')
    int totalRows
    long offset

    List<ResultObject<T, V>> rows

    static class ResultObject<T, V> {
        String id
        String key
        T value
        V doc
    }

    static class AllResultEntity {
        String rev
    }
}
