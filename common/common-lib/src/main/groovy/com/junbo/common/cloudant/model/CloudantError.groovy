package com.junbo.common.cloudant.model

import groovy.transform.CompileStatic

/**
 * CloudantError.
 */
@CompileStatic
class CloudantError implements CloudantModel {
    String error
    String reason
}
