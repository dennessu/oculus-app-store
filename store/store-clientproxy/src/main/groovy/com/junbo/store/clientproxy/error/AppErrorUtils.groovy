package com.junbo.store.clientproxy.error

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.AppError
import com.junbo.common.error.AppErrorException
import com.junbo.common.error.ErrorDetail
import com.junbo.store.spec.error.AppErrors
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.Assert

/**
 * The AppErrorUtils class.
 */
@CompileStatic
@Component('storeAppErrorUtils')
class AppErrorUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppErrorUtils)

    private static final String Invalid_Error_Code = '001'

    public void throwOnFieldInvalidError(ErrorContext errorContext, Throwable ex, String ... components) {
        Assert.notNull(components)
        if (!(ex instanceof AppErrorException)) {
            return;
        }

        AppError appError = ((AppErrorException) ex).error
        boolean couldHandle = false
        for (String component : components) {
            if ("${component}.${Invalid_Error_Code}" == appError.error().code) {
                couldHandle = true
                break
            }
        }

        if (couldHandle) {
            LOGGER.error('name=Invalid_Field_Error', ex)

            AppError resultAppError
            if (errorContext.fieldName != null) {
                resultAppError = AppCommonErrors.INSTANCE.fieldInvalid(
                        appError.error().details.collect { ErrorDetail errorDetail -> return new ErrorDetail(errorContext.fieldName, errorDetail.reason) }.toArray(new ErrorDetail[0])
                )
            } else {
                resultAppError = AppCommonErrors.INSTANCE.fieldInvalid(appError.error().details.toArray(new ErrorDetail[0]))
            }
            throw resultAppError.exception()
        }
    }

    public void throwUnknownError(String operation, Throwable ex) {
        if (isStoreError(ex)) { // error thrown by store
            throw ex
        }
        LOGGER.error('nam=StoreApiUnknownError, operation={}', operation, ex)
        throw AppErrors.INSTANCE.unknownError().exception()
    }

    public boolean isStoreError(Throwable ex) {
        String errorCode = getAppErrorCode(ex)
        return errorCode != null && errorCode.startsWith(ErrorCodes.Store.majorCode)
    }

    public String getAppErrorCode(Throwable ex) {
        if (!(ex instanceof AppErrorException)) {
            return null
        }
        return ((AppErrorException) ex).error?.error()?.code
    }

    public boolean isAppError(Throwable ex, String... codes) {
        if (!(ex instanceof AppErrorException)) {
            return false
        }
        String code = ((AppErrorException)ex).error?.error()?.code
        if (code == null) {
            return false
        }
        return codes.any {String c -> c == code}
    }
}
