package com.junbo.store.clientproxy.casey
import com.junbo.langur.core.client.ExceptionHandler
import com.junbo.store.spec.exception.casey.CaseyException
import com.ning.http.client.Response
import groovy.transform.CompileStatic
/**
 * The CaseyExceptionHandler class.
 */
@CompileStatic
class CaseyExceptionHandler implements ExceptionHandler {

    @Override
    void handleExceptionResponse(Response response) {
        throw new CaseyException("Casey_Return_Error,response:\n" + response.getResponseBody())
    }
}