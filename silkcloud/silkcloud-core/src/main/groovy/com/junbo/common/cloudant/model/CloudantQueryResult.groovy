package com.junbo.common.cloudant.model

import com.junbo.common.cloudant.json.annotations.CloudantProperty
import groovy.transform.CompileStatic
/**
 * CloudantQueryResult.
 */

@CompileStatic
class CloudantQueryResult<T, V> {
    @CloudantProperty('total_rows')
    Long totalRows
    Long offset
    String bookmark

    List<ResultObject<T, V>> rows

    static class ResultObject<T, V> {
        String id
        Object key
        T value
        V doc
    }

    static class AllResultEntity {
        String rev
    }
}
