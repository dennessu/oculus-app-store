package com.junbo.identity.core.service.validator.impl

import com.junbo.identity.core.service.validator.QQValidator
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserQQ
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/26/14.
 */
@CompileStatic
class QQValidatorImpl implements QQValidator {
    private Integer minQQLength;
    private Integer maxQQLength;

    @Override
    void validate(UserQQ userQQ) {
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
