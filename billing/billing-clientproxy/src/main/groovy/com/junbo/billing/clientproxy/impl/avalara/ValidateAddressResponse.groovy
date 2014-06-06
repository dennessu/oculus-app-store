package com.junbo.billing.clientproxy.impl.avalara

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic

/**
 * Created by LinYi on 14-3-24.
 */
@CompileStatic
class ValidateAddressResponse {
    @JsonProperty('Address')
    ValidatedAddress address
    @JsonProperty('ResultCode')
    SeverityLevel resultCode
    @JsonProperty('Messages')
    ResponseMessage[] messages
}
