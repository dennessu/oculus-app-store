package com.junbo.cart.test.util

import com.ning.http.client.Response
import groovy.transform.CompileStatic

/**
 * Created by fzhang on 5/12/2014.
 */
@CompileStatic
class ExceptionHandler implements com.junbo.langur.core.client.ExceptionHandler {

    @Override
    void handleExceptionResponse(Response response) {
        throw new RuntimeException('exception in api call')
    }
}
