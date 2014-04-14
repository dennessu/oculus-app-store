package com.junbo.common.cloudant.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic

/**
 * CloudantResponse.
 */
@CompileStatic
class CloudantResponse {
    Boolean ok
    String id
    @JsonProperty('rev')
    String revision
}
