package com.goshop.catalog.rest.intercept

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import com.goshop.catalog.common.error.AppErrors

import javax.ws.rs.WebApplicationException
import javax.ws.rs.ext.Provider
import javax.ws.rs.ext.ReaderInterceptor
import javax.ws.rs.ext.ReaderInterceptorContext

@Provider
class InvalidJsonReaderInterceptor implements ReaderInterceptor {

    @Override
    Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        try {
            return context.proceed();
        } catch (JsonParseException | JsonMappingException ex) {

            // todo: customize ex.getMessage() to hide internal information.
            throw AppErrors.INSTANCE.invalidJson(ex.getMessage()).exception()
        }
    }
}
