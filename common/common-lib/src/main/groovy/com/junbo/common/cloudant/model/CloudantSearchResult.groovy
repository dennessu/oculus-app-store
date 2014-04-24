package com.junbo.common.cloudant.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic

/**
 * CloudantSearchResult.
 */

@CompileStatic
class CloudantSearchResult<T> implements CloudantModel {
    @JsonProperty('total_rows')
    Long totalRows
    Long offset

    List<ResultObject<T>> rows

    static class ResultObject<T> {
        String id
        String key
        T value
    }

    static class AllResultEntity {
        String rev
    }
}
