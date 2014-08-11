package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.normalize.NormalizeService
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.core.service.validator.UsernameValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.v1.model.UserLoginName
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 8/6/14.
 */
@CompileStatic
class UserLoginNameValidatorImpl implements PiiValidator {

    private UsernameValidator usernameValidator
    private NormalizeService normalizeService


    @Override
    boolean handles(String type) {
        if (type == UserPersonalInfoType.USERNAME.toString()) {
            return true
        }
        return false
    }

    @Override
    Promise<Void> validateCreate(JsonNode value, UserId userId, OrganizationId organizationId) {
        UserLoginName loginName = (UserLoginName)JsonHelper.jsonNodeToObj(value, UserLoginName)
        checkBasicUserName(loginName)
        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateUpdate(JsonNode value, JsonNode oldValue) {
        UserLoginName loginName = (UserLoginName)JsonHelper.jsonNodeToObj(value, UserLoginName)
        UserLoginName oldLoginName = (UserLoginName)JsonHelper.jsonNodeToObj(oldValue, UserLoginName)
        checkBasicUserName(loginName)
        if (loginName.userName != oldLoginName.userName) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('value', 'value can\'t be updated').exception()
        }

        return Promise.pure(null)
    }

    @Override
    JsonNode updateJsonNode(JsonNode value) {
        UserLoginName userLoginName = (UserLoginName)JsonHelper.jsonNodeToObj(value, UserLoginName)
        userLoginName.canonicalUsername = normalizeService.normalize(userLoginName.userName)
        return ObjectMapperProvider.instance().valueToTree(userLoginName)
    }

    private void checkBasicUserName(UserLoginName userLoginName) {
        usernameValidator.validateUsername(userLoginName.userName)
    }

    @Required
    void setUsernameValidator(UsernameValidator usernameValidator) {
        this.usernameValidator = usernameValidator
    }

    @Required
    void setNormalizeService(NormalizeService normalizeService) {
        this.normalizeService = normalizeService
    }
}
