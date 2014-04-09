package com.junbo.common.cloudant.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic

/**
 * CloudantViews.
 */
@CompileStatic
@JsonInclude(JsonInclude.Include.NON_NULL)
class CloudantViews {
    @JsonProperty('_id')
    String id

    @JsonProperty('_rev')
    String revision

    String language = 'javascript'

    Map<String, CloudantView> views

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class CloudantView {
        String map
        String reduce
        @JsonIgnore
        Class resultClass
    }
}
