package com.goshop.catalog.rest.error

import com.goshop.catalog.common.error.AppError
import com.goshop.catalog.common.error.AppErrorException
import com.goshop.catalog.spec.model.error.Error
import groovy.transform.CompileStatic

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Provider
@CompileStatic
class ErrorExceptionMapper implements ExceptionMapper<AppErrorException> {

    @Override
    Response toResponse(AppErrorException exception) {
        AppError error = exception.error;

        return Response.status(error.httpStatusCode)
                .entity(mapToError(error))
                .type(MediaType.APPLICATION_JSON)
                .build()
    }

    private Error mapToError(AppError appError) {

        Error error = new Error(
                code: appError.code,
                message: appError.message,
                field: appError.field
        );

        if (appError.causes != null) {
            error.causes = appError.causes.collect { AppError cause ->
                mapToError(cause)
            }
        }

        return error
    }
}
