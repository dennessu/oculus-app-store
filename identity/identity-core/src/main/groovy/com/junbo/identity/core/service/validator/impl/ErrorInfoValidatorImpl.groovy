package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.ErrorIdentifier
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.common.util.ValidatorUtil
import com.junbo.identity.core.service.validator.ErrorInfoValidator
import com.junbo.identity.data.repository.ErrorInfoRepository
import com.junbo.identity.spec.v1.model.ErrorDetail
import com.junbo.identity.spec.v1.model.ErrorInfo
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 7/23/14.
 */
@CompileStatic
class ErrorInfoValidatorImpl implements ErrorInfoValidator {
    private ErrorInfoRepository errorInfoRepository

    @Override
    Promise<ErrorInfo> validateForGet(ErrorIdentifier identifier) {
        if (identifier == null) {
            throw new IllegalArgumentException('id can\'t be null')
        }
        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(ErrorInfo errorInfo) {
        basicCheck(errorInfo)

        errorInfo.id = new ErrorIdentifier(errorInfo.errorIdentifier)

        return errorInfoRepository.get(new ErrorIdentifier(errorInfo.errorIdentifier)).then { ErrorInfo existing ->
            if (existing != null) {
                throw AppCommonErrors.INSTANCE.fieldDuplicate('errorIdentifier').exception()
            }

            return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateForUpdate(ErrorIdentifier identifier, ErrorInfo errorInfo, ErrorInfo oldErrorInfo) {
        basicCheck(errorInfo)

        if (errorInfo.errorIdentifier != oldErrorInfo.errorIdentifier) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('errorIdentifier', 'Can\'t change errorIdentifier').exception()
        }

        if (errorInfo.errorIdentifier != identifier.toString()) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('errorIdentifier', 'Can\'t change errorIdentifier').exception()
        }

        return Promise.pure(null)
    }


    private void basicCheck(ErrorInfo errorInfo) {
        if (errorInfo == null) {
            throw new IllegalArgumentException('errorInfo can\'t be null')
        }

        if (errorInfo.errorIdentifier == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('errorIdentifier').exception()
        }

        if (errorInfo.locales == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('locales').exception()
        }

        errorInfo.locales.entrySet().each { Map.Entry<String, JsonNode> entry ->
            if (entry.key == null || entry.value == null) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('locales').exception()
            }

            if (!ValidatorUtil.isValidLocale(entry.key)) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('locales', 'Invalid locale').exception()
            }

            ErrorDetail errorDetail = (ErrorDetail)JsonHelper.jsonNodeToObj(entry.value, ErrorDetail)
            if (errorDetail.errorInformation == null) {
                throw AppCommonErrors.INSTANCE.fieldRequired('locales.errorInformation').exception()
            }
            if (errorDetail.errorSummary == null) {
                throw AppCommonErrors.INSTANCE.fieldRequired('locales.errorSummary').exception()
            }
            if (errorDetail.errorTitle == null) {
                throw AppCommonErrors.INSTANCE.fieldRequired('locales.errorTitle').exception()
            }
        }
    }

    @Required
    void setErrorInfoRepository(ErrorInfoRepository errorInfoRepository) {
        this.errorInfoRepository = errorInfoRepository
    }
}
