/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.clientproxy.google

import com.junbo.langur.core.client.ExceptionHandler
import com.junbo.langur.core.client.MessageTranscoder
import com.junbo.langur.core.client.TypeReference
import com.ning.http.client.Response
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * GoogleExceptionHandler.
 */
@CompileStatic
class GoogleExceptionHandler implements ExceptionHandler {
    private static final GoogleError UNEXPECTED_ERROR = new GoogleError(message: 'unexpected error')

    private MessageTranscoder messageTranscoder

    @Required
    void setMessageTranscoder(MessageTranscoder messageTranscoder) {
        this.messageTranscoder = messageTranscoder
    }

    @Override
    void handleExceptionResponse(Response response) {
        try {
            GoogleError error = messageTranscoder.<GoogleError>decode(new TypeReference<GoogleError>() { },
                    response.responseBody) as GoogleError;
            throw new GoogleException(error)
        } catch (IOException e) {
            throw new GoogleException(UNEXPECTED_ERROR, e)
        }
    }
}
