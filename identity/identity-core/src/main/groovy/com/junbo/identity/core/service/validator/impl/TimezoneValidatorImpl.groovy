package com.junbo.identity.core.service.validator.impl

import com.junbo.identity.core.service.validator.TimezoneValidator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Check timeZone valid.
 * Created by kg on 3/18/14.
 * REF: http://en.wikipedia.org/wiki/Tz_database
 */
@CompileStatic
class TimezoneValidatorImpl implements TimezoneValidator {

    private List<String> validTimezones

    private String defaultTimezone

    @Required
    void setValidTimezones(List<String> validTimezones) {
        this.validTimezones = validTimezones
    }

    @Required
    void setDefaultTimezone(String defaultTimezone) {
        this.defaultTimezone = defaultTimezone
    }

    boolean isValidTimezone(String timezone) {
        if (timezone == null) {
            throw new IllegalArgumentException('timezone is null')
        }

        return validTimezones.contains(timezone)
    }

    String getDefaultTimezone() {
        return defaultTimezone
    }
}
