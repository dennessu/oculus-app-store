package com.junbo.common.cloudant.model

import com.junbo.common.cloudant.json.annotations.CloudantProperty
import groovy.transform.CompileStatic

/**
 * CloudantResponse.
 */
@CompileStatic
class CloudantResponse {
    Boolean ok
    String id
    @CloudantProperty('rev')
    String revision
}
