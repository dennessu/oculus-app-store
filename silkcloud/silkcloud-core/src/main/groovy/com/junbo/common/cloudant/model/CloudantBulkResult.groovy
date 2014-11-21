package com.junbo.common.cloudant.model

import groovy.transform.CompileStatic
/**
 * CloudantResponse.
 */
@CompileStatic
class CloudantBulkResult {
    String id;
    String rev;
    String error;
    String reason;
}
