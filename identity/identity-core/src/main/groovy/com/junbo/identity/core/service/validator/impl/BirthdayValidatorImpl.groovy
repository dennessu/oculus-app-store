package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserDOB
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by kg on 3/17/14.
 */
@CompileStatic
class BirthdayValidatorImpl implements PiiValidator {

    private Integer timespanInYears

    @Override
    boolean handles(String type) {
        if (type == UserPersonalInfoType.DOB.toString()) {
            return true
        }
        return false
    }

    @Override
    Promise<Void> validate(JsonNode value, UserId userId) {
        UserDOB userDOB = ObjectMapperProvider.instance().treeToValue(value, UserDOB)
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

        return Promise.pure(null)
    }

    @Required
    void setTimespanInYears(Integer timespanInYears) {
        this.timespanInYears = timespanInYears
    }
}
