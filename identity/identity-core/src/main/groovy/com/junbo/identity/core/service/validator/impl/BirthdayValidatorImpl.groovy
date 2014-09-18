package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserDOB
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * 1):  Check not null;
 * 2):  Check the age in an range.
 * Created by kg on 3/17/14.
 */
@CompileStatic
class BirthdayValidatorImpl implements PiiValidator {

    private Integer timespanMinInYears
    private Integer timespanMaxInYears

    @Override
    boolean handles(String type) {
        if (type == UserPersonalInfoType.DOB.toString()) {
            return true
        }
        return false
    }

    @Override
    Promise<Void> validateCreate(JsonNode value, UserId userId, OrganizationId organizationId) {
        UserDOB userDOB = (UserDOB)JsonHelper.jsonNodeToObj(value, UserDOB)
        checkBirthdayInfo(userDOB)

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateUpdate(JsonNode value, JsonNode oldValue) {
        UserDOB userDOB = (UserDOB)JsonHelper.jsonNodeToObj(value, UserDOB)
        UserDOB oldUserDOB = (UserDOB)JsonHelper.jsonNodeToObj(oldValue, UserDOB)

        if (userDOB != oldUserDOB) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('dob', 'value can\'t be updated.').exception()
        }

        return Promise.pure(null)
    }

    private void checkBirthdayInfo(UserDOB userDOB) {
        Date birthday = userDOB.info
        if (birthday == null) {
            throw new IllegalArgumentException('birthday is null')
        }

        def after = Calendar.instance
        after.add(Calendar.YEAR, -timespanMinInYears)
        if (birthday.after(after.time)) {
            throw AppErrors.INSTANCE.ageRestrictionError().exception()
        }

        def before = Calendar.instance
        before.add(Calendar.YEAR, -timespanMaxInYears)
        if (birthday.before(before.time)) {
            throw AppErrors.INSTANCE.ageRestrictionError().exception()
        }
    }

    @Override
    JsonNode updateJsonNode(JsonNode value) {
        return value
    }

    @Required
    void setTimespanMinInYears(Integer timespanMinInYears) {
        this.timespanMinInYears = timespanMinInYears
    }

    @Required
    void setTimespanMaxInYears(Integer timespanMaxInYears) {
        this.timespanMaxInYears = timespanMaxInYears
    }
}
