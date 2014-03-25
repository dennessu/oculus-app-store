package com.junbo.identity.core.service.validator

/**
 * Created by kg on 3/18/14.
 */
interface TimezoneValidator {

    boolean isValidTimezone(String timezone)

    String getDefaultTimezone()
}
