package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
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
    Promise<Void> validateCreate(JsonNode value, UserId userId, OrganizationId organizationId) {
        UserDriverLicense userDriverLicense = (UserDriverLicense)JsonHelper.jsonNodeToObj(value, UserDriverLicense)
        checkDriverLicense(userDriverLicense)
        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateUpdate(JsonNode value, JsonNode oldValue) {
        UserDriverLicense userDriverLicense = (UserDriverLicense)JsonHelper.jsonNodeToObj(value, UserDriverLicense)
        UserDriverLicense oldUserDriverLicense = (UserDriverLicense)JsonHelper.jsonNodeToObj(oldValue,
                UserDriverLicense)

        if (userDriverLicense != oldUserDriverLicense) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('value', 'value can\'t be updated.').exception()
        }
        return Promise.pure(null)
    }

    private void checkDriverLicense(UserDriverLicense userDriverLicense) {
        if (userDriverLicense.info != null) {
            if (userDriverLicense.info.length() > maxDriverLicenseLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('value.info', maxDriverLicenseLength).exception()
            }
            if (userDriverLicense.info.length() < minDriverLicenseLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('value.info', minDriverLicenseLength).exception()
            }
        }
    }

    @Override
    JsonNode updateJsonNode(JsonNode value) {
        return value
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
