package com.junbo.common.cloudant.model

/**
 * Created by liangfu on 8/11/2014.
 */
class CloudantReduceQueryResult {

    List<ReduceResultObject> rows

    static class ReduceResultObject {
        Object key
        Integer value
    }
}
