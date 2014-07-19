package com.junbo.oauth.api.mapper

import com.junbo.common.error.AppCommonErrors
import com.junbo.langur.core.webflow.ConversationNotfFoundException
import com.junbo.oauth.core.exception.AppErrors
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
        return AppCommonErrors.INSTANCE.fieldInvalid('cid').exception().response
    }
}
