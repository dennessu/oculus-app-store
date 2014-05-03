/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.clientproxy.facebook

import com.junbo.langur.core.client.ExceptionHandler
import com.junbo.langur.core.client.MessageTranscoder
import com.junbo.langur.core.client.TypeReference
import com.ning.http.client.Response
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * FacebookExceptionHandler.
 */
@CompileStatic
class FacebookExceptionHandler implements ExceptionHandler {
    private static final FacebookError UNEXPECTED_ERROR =
            new FacebookError(error: new FacebookError.Error(message: 'Unexpected error'))

    private MessageTranscoder messageTranscoder

    @Required
    void setMessageTranscoder(MessageTranscoder messageTranscoder) {
        this.messageTranscoder = messageTranscoder
    }

    @Override
    void handleExceptionResponse(Response response) {
        try {
            FacebookError error = messageTranscoder.<FacebookError> decode(new TypeReference<FacebookError>() { },
                    response.responseBody) as FacebookError;
            throw new FacebookException(error)
        } catch (IOException e) {
            throw new FacebookException(UNEXPECTED_ERROR, e)
        }
    }
}
