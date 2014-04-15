package com.junbo.oauth.api.mapper

import com.junbo.langur.core.webflow.ConversationNotfFoundException
import com.junbo.oauth.core.exception.AppExceptions
import groovy.transform.CompileStatic

import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

/**
 * Created by Shenhua on 4/7/2014.
 */
@Provider
@CompileStatic
class ConversationNotFoundExceptionMapper implements ExceptionMapper<ConversationNotfFoundException> {

    @Override
    Response toResponse(ConversationNotfFoundException exception) {
        return AppExceptions.INSTANCE.invalidCid().exception().response
    }
}
