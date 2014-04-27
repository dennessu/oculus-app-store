package com.junbo.identity.core.service.validator.impl

import com.junbo.identity.core.service.validator.BirthdayValidator
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserDOB
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by kg on 3/17/14.
 */
@CompileStatic
class BirthdayValidatorImpl implements BirthdayValidator {

    private Integer timespanInYears

    @Override
    void validate(UserDOB userDOB) {
        Date birthday = userDOB.birthday
        if (birthday == null) {
            throw new IllegalArgumentException('birthday is null')
        }

        def after = Calendar.instance

        if (birthday.after(after.time)) {
            throw AppErrors.INSTANCE.fieldInvalid('DOB').exception()
        }

        def before = Calendar.instance
        before.add(Calendar.YEAR, -timespanInYears)

        if (birthday.before(before.time)) {
            throw AppErrors.INSTANCE.fieldInvalid('DOB').exception()
        }
    }

    @Required
    void setTimespanInYears(Integer timespanInYears) {
        this.timespanInYears = timespanInYears
    }
}
