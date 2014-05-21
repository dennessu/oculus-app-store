package com.junbo.common.cloudant.model

import com.junbo.common.cloudant.json.annotations.CloudantIgnore
import com.junbo.common.cloudant.json.annotations.CloudantProperty
import groovy.transform.CompileStatic
/**
 * CloudantViews.
 */
@CompileStatic
class CloudantViews {
    @CloudantProperty('_id')
    String id

    @CloudantProperty('_rev')
    String revision

    String language = 'javascript'

    Map<String, CloudantView> views
    Map<String, CloudantIndex> indexes

    static class CloudantView {
        String map
        String reduce
        @CloudantIgnore
        Class resultClass
    }

    static class CloudantIndex {
        String index
        @CloudantIgnore
        Class resultClass
    }
}
