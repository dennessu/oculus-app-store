package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.v1.model.UserQQ
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/26/14.
 */
@CompileStatic
class QQValidatorImpl implements PiiValidator {
    private Integer minQQLength;
    private Integer maxQQLength;

    @Override
    boolean handles(String type) {
        if (type == UserPersonalInfoType.QQ.toString()) {
            return true
        }
        return false
    }

    @Override
    Promise<Void> validateCreate(JsonNode value, UserId userId, OrganizationId organizationId) {
        UserQQ userQQ = (UserQQ)JsonHelper.jsonNodeToObj(value, UserQQ)
        checkUserQQ(userQQ)
        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateUpdate(JsonNode value, JsonNode oldValue) {
        UserQQ userQQ = (UserQQ)JsonHelper.jsonNodeToObj(value, UserQQ)
        UserQQ oldUserQQ = (UserQQ)JsonHelper.jsonNodeToObj(oldValue, UserQQ)

        if (userQQ != oldUserQQ) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('value', 'value can\'t be updated.').exception()
        }
        return Promise.pure(null)
    }

    private void checkUserQQ(UserQQ userQQ) {
        if (userQQ.info != null) {
            if (userQQ.info.length() > maxQQLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('value.info', maxQQLength).exception()
            }

            if (userQQ.info.length() < minQQLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('value.info', minQQLength).exception()
            }
        }
    }

    @Required
    void setMinQQLength(Integer minQQLength) {
        this.minQQLength = minQQLength
    }

    @Required
    void setMaxQQLength(Integer maxQQLength) {
        this.maxQQLength = maxQQLength
    }
}
