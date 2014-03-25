package com.junbo.common.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import java.io.IOException;

@Provider
public class InvalidJsonReaderInterceptor implements ReaderInterceptor {

    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        try {
            return context.proceed();
        } catch (JsonParseException | JsonMappingException ex) {

            // todo: customize ex.getMessage() to hide internal information.
            throw ERRORS.invalidJson(ex.getMessage()).exception();
        }
    }

    public static final Errors ERRORS = ErrorProxy.newProxyInstance(Errors.class);

    public static interface Errors {

        @ErrorDef(httpStatusCode = 400, code = "10001", description = "invalid Json: {0}", field = "request.body")
        AppError invalidJson(String detail);
    }
}
