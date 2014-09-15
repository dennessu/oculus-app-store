package com.junbo.identity.core.service.validator

import com.junbo.common.id.ErrorIdentifier
import com.junbo.identity.spec.v1.model.ErrorInfo
import com.junbo.identity.spec.v1.option.list.ErrorInfoListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 7/23/14.
 */
@CompileStatic
public interface ErrorInfoValidator {
    Promise<ErrorInfo> validateForGet(ErrorIdentifier identifier)
    Promise<Void> validateForCreate(ErrorInfo errorInfo)
    Promise<Void> validateForUpdate(ErrorIdentifier identifier, ErrorInfo errorInfo, ErrorInfo oldErrorInfo)
    Promise<Void> validateForSearch(ErrorInfoListOptions listOptions)
}