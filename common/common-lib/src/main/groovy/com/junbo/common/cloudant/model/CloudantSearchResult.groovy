package com.junbo.common.cloudant.model

import com.junbo.common.cloudant.json.annotations.CloudantProperty
import groovy.transform.CompileStatic
/**
 * CloudantSearchResult.
 */

@CompileStatic
class CloudantSearchResult<T> {
    @CloudantProperty('total_rows')
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
