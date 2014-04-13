package com.junbo.identity.core.service.validator

import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/10/14.
 */
@CompileStatic
interface CurrencyValidator {
    boolean isValid(String currency)

    String getDefault()
}