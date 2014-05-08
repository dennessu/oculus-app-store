package com.junbo.identity.core.service.validator.impl

import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.core.service.validator.PiiValidatorFactory
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/29/14.
 */
@CompileStatic
class PiiValidatorFactoryImpl implements PiiValidatorFactory {

    private final List<PiiValidator> piiValidatorList

    PiiValidatorFactoryImpl(List<PiiValidator> piiValidatorList) {
        this.piiValidatorList = piiValidatorList
    }

    @Override
    List<PiiValidator> getValidators() {
        return this.piiValidatorList
    }
}
