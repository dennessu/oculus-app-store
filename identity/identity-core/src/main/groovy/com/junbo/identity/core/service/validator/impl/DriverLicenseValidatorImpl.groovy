package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.id.UserId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserDriverLicense
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/26/14.
 */
@CompileStatic
class DriverLicenseValidatorImpl implements PiiValidator {

    private Integer minDriverLicenseLength
    private Integer maxDriverLicenseLength

    @Override
    boolean handles(String type) {
        if (type == UserPersonalInfoType.DRIVERS_LICENSE.toString()) {
            return true
        }
        return false
    }

    @Override
    Promise<Void> validateCreate(JsonNode value, UserId userId) {
        UserDriverLicense userDriverLicense = (UserDriverLicense)JsonHelper.jsonNodeToObj(value, UserDriverLicense)
        checkDriverLicense(userDriverLicense)
        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateUpdate(JsonNode value, JsonNode oldValue, UserId userId) {
        UserDriverLicense userDriverLicense = (UserDriverLicense)JsonHelper.jsonNodeToObj(value, UserDriverLicense)
        UserDriverLicense oldUserDriverLicense = (UserDriverLicense)JsonHelper.jsonNodeToObj(oldValue,
                UserDriverLicense)

        if (userDriverLicense != oldUserDriverLicense) {
            throw AppErrors.INSTANCE.fieldInvalidException('value', 'value can\'t be updated.').exception()
        }
        return Promise.pure(null)
    }

    private void checkDriverLicense(UserDriverLicense userDriverLicense) {
        if (userDriverLicense.info != null) {
            if (userDriverLicense.info.length() > maxDriverLicenseLength) {
                throw AppErrors.INSTANCE.fieldTooLong('value.info', maxDriverLicenseLength).exception()
            }
            if (userDriverLicense.info.length() < minDriverLicenseLength) {
                throw AppErrors.INSTANCE.fieldTooShort('value.info', minDriverLicenseLength).exception()
            }
        }
    }

    @Required
    void setMinDriverLicenseLength(Integer minDriverLicenseLength) {
        this.minDriverLicenseLength = minDriverLicenseLength
    }

    @Required
    void setMaxDriverLicenseLength(Integer maxDriverLicenseLength) {
        this.maxDriverLicenseLength = maxDriverLicenseLength
    }
}
