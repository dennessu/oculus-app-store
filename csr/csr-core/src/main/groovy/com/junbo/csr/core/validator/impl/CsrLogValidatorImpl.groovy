package com.junbo.csr.core.validator.impl

import com.junbo.common.id.CsrLogId
import com.junbo.csr.common.ValidatorUtil
import com.junbo.csr.core.validator.CsrLogValidator
import com.junbo.csr.db.repo.CsrLogRepository
import com.junbo.csr.spec.def.CsrLogActionType
import com.junbo.csr.spec.error.AppErrors
import com.junbo.csr.spec.model.CsrLog
import com.junbo.csr.spec.option.list.CsrLogListOptions
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-7-4.
 */
@CompileStatic
class CsrLogValidatorImpl implements CsrLogValidator {
    private CsrLogRepository csrLogRepository
    private UserResource userResource
    private Integer maxSearchDays
    private Integer regardingMaxLength

    @Required
    void setCsrLogRepository(CsrLogRepository csrLogRepository) {
        this.csrLogRepository = csrLogRepository
    }

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Required
    void setMaxSearchDays(Integer maxSearchDays) {
        this.maxSearchDays = maxSearchDays
    }

    @Required
    void setRegardingMaxLength(Integer regardingMaxLength) {
        this.regardingMaxLength = regardingMaxLength
    }

    @Override
    Promise<CsrLog> validateForGet(CsrLogId csrLogId) {
        if (csrLogId == null || csrLogId.value == null) {
            throw new IllegalArgumentException('csrLogId is null')
        }

        return csrLogRepository.get(csrLogId).then { CsrLog csrLog ->
            if (csrLog == null) {
               throw AppErrors.INSTANCE.csrLogNotFound(csrLogId).exception()
            }

            return Promise.pure(csrLog)
        }
    }

    @Override
    Promise<Void> validateForSearch(CsrLogListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('csr log listOptions is null')
        }

        if (options.lastHours != null && (options.from != null || options.to != null)) {
            throw AppErrors.INSTANCE.invalidCsrLogSearch().exception()
        }

        if (options.lastHours == null) {
            if (options.from == null || options.to == null) {
                throw AppErrors.INSTANCE.invalidCsrLogSearch().exception()
            }
        }

        if (options.from.after(options.to) || options.lastHours <= 0) {
            throw AppErrors.INSTANCE.invalidCsrLogSearch().exception()
        }

        if (options.from != null) {
            Calendar calendar = Calendar.instance
            calendar.setTime(options.from)
            calendar.add(Calendar.DATE, this.maxSearchDays)
            if (options.to.after(calendar.time)) {
                throw AppErrors.INSTANCE.exceedCsrLogSearchRange().exception()
            }
        }

        if (options.lastHours != null) {
            if (options.lastHours/24 > (this.maxSearchDays - 1)) {
                throw AppErrors.INSTANCE.exceedCsrLogSearchRange().exception()
            }
        }

        if (options.countryCode != null && !ValidatorUtil.isValidCountryCode(options.countryCode)) {
            throw AppErrors.INSTANCE.invalidCountryCode().exception()
        }

        if (options.action != null) {
            List<String> allowedActionType = CsrLogActionType.values().collect { CsrLogActionType type ->
                return type.toString()
            }

            if (!(options.action in allowedActionType)) {
                throw AppErrors.INSTANCE.fieldInvalid('action', allowedActionType.join(',')).exception()
            }
        }

        if (options.userId != null) {
            return userResource.get(options.userId, new UserGetOptions()).then { User user ->
                if (user == null) {
                    throw AppErrors.INSTANCE.userNotFound().exception()
                }

                return Promise.pure(null)
            }
        }
    }

    @Override
    Promise<Void> validateForCreate(CsrLog csrLog) {
        if (csrLog.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }

        if (csrLog.countryCode == null) {
            throw AppErrors.INSTANCE.fieldRequired('countryCode').exception()
        }
        else if (!ValidatorUtil.isValidCountryCode(csrLog.countryCode)) {
            throw AppErrors.INSTANCE.invalidCountryCode().exception()
        }

        if (csrLog.action == null) {
            throw AppErrors.INSTANCE.fieldRequired('action').exception()
        }

        if (csrLog.regarding == null) {
            throw AppErrors.INSTANCE.fieldRequired('regarding').exception()
        }
        else if (csrLog.regarding.length() > this.regardingMaxLength) {
            throw AppErrors.INSTANCE.fieldInvalid('regarding').exception()
        }

        if (csrLog.userId == null) {
            throw AppErrors.INSTANCE.fieldRequired('userId').exception()
        }
        else {
            return userResource.get(csrLog.userId, new UserGetOptions()).then { User user ->
                if (user == null) {
                    throw AppErrors.INSTANCE.userNotFound().exception()
                }

                return Promise.pure(null)
            }
        }
    }
}
