package com.junbo.identity.core.service.validator.impl

import com.junbo.identity.core.service.validator.AddressValidator
import com.junbo.identity.spec.v1.model.Address
import groovy.transform.CompileStatic

/**
 * Created by xmchen on 14-4-15.
 */
@CompileStatic
class AddressValidatorImpl implements AddressValidator {

    @Override
    void validate(Address address) {
        //todo: Need to implement whether this is valid
    }
}
