package com.junbo.identity.core.service.validator

/**
 * Created by kg on 3/17/14.
 */
interface LocaleValidator {

    boolean isValidLocale(String locale)

    String getDefaultLocale()

}
