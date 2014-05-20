package com.junbo.entitlement.db.dao.cloudant

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic

/**
 * CloudantViews. Copied from common.
 */
@CompileStatic
class EntitlementCloudantViews {
    @JsonProperty('_id')
    String id

    @JsonProperty('_rev')
    String revision

    String language = 'javascript'

    Map<String, CloudantView> views
    Map<String, CloudantView> indexes

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class CloudantView {
        String index
        String map
        String reduce
        @JsonIgnore
        Class resultClass
    }
}