package com.junbo.identity.core.service.validator

import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/29/14.
 */
@CompileStatic
interface PiiValidatorFactory {
    public List<PiiValidator> getValidators()
}
