package com.junbo.common.cloudant.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic

/**
 * CloudantSearchResult.
 */

@CompileStatic
class CloudantSearchResult<T> {
    @JsonProperty('total_rows')
    Integer totalRows
    Long offset

    List<ResultObject<T>> rows

    static class ResultObject<T> {
        Long id
        String key
        T value
    }

    static class AllResultEntity {
        String rev
    }
}
