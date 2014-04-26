package com.junbo.identity.core.service.validator

import com.junbo.identity.spec.v1.model.Address
import groovy.transform.CompileStatic

/**
 * Created by xmchen on 14-4-15.
 */
@CompileStatic
interface AddressValidator {
    void validate(Address address)
}