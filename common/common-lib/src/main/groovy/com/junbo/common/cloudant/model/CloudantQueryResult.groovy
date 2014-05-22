package com.junbo.common.cloudant.model

import com.junbo.common.cloudant.json.annotations.CloudantProperty
import groovy.transform.CompileStatic
/**
 * CloudantQueryResult.
 */

@CompileStatic
class CloudantQueryResult<T> {
    @CloudantProperty('total_rows')
    Long totalRows
    Long offset
    String bookmark

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
