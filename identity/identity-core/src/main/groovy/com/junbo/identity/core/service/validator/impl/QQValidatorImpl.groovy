package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserQQ
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
    void validate(JsonNode value) {
        UserQQ userQQ = ObjectMapperProvider.instance().treeToValue(value, UserQQ)
        if (userQQ.qq != null) {
            if (userQQ.qq.length() > maxQQLength) {
                throw AppErrors.INSTANCE.fieldTooLong('qq', maxQQLength).exception()
            }

            if (userQQ.qq.length() < minQQLength) {
                throw AppErrors.INSTANCE.fieldTooShort('qq', minQQLength).exception()
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
