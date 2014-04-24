package com.junbo.common.cloudant.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic

/**
 * CloudantResponse.
 */
@CompileStatic
class CloudantResponse implements CloudantModel {
    Boolean ok
    String id
    @JsonProperty('rev')
    String revision
}
