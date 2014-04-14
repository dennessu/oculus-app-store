package com.junbo.identity.core.service.validator.impl

import com.junbo.identity.core.service.validator.BirthdayValidator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by kg on 3/17/14.
 */
@CompileStatic
class BirthdayValidatorImpl implements BirthdayValidator {

    private Integer timespanInYears

    @Required
    void setTimespanInYears(Integer timespanInYears) {
        this.timespanInYears = timespanInYears
    }

    boolean isValidBirthday(Date birthday) {
        if (birthday == null) {
            throw new IllegalArgumentException('birthday is null')
        }

        def after = Calendar.instance

        if (birthday.after(after.time)) {
            return false
        }

        def before = Calendar.instance
        before.add(Calendar.YEAR, -timespanInYears)

        if (birthday.before(before.time)) {
            return false
        }

        return true
    }
}
