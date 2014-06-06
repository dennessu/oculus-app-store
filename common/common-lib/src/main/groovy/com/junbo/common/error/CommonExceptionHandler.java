/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.error;

import com.junbo.langur.core.client.ClientResponseException;
import com.junbo.langur.core.client.ExceptionHandler;
import com.junbo.langur.core.client.MessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.ning.http.client.Response;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

/**
 * CommonExceptionHandler.
 */
public class CommonExceptionHandler implements ExceptionHandler {
    private MessageTranscoder messageTranscoder;

    @Required
    public void setMessageTranscoder(MessageTranscoder messageTranscoder) {
        this.messageTranscoder = messageTranscoder;
    }

    @Override
    public void handleExceptionResponse(Response response) {
        try {
            Error error = messageTranscoder.decode(new TypeReference<Error>() {}, response.getResponseBody());

            if (error == null) {
                throw new ClientResponseException("Error response is null or empty", response);
            }

            throw getAppError(response.getStatusCode(), error).exception();
        } catch (java.io.IOException e) {
            throw new ClientResponseException("Unable to unmarshall the error response", response, e);
        }
    }

    private AppError getAppError(final int statusCode, final Error error) {
        if (error == null) {
            throw new IllegalArgumentException("error is null");
        }

        return new AppError() {
            @Override
            public int getHttpStatusCode() {
                return statusCode;
            }

            @Override
            public String getCode() {
                return error.getCode();
            }

            @Override
            public String getDescription() {
                return error.getDescription();
            }

            @Override
            public String getField() {
                return error.getField();
            }

            @Override
            public List<AppError> getCauses() {
                List<AppError> causes = new ArrayList<>();
                if (error.getCauses() != null) {
                    for (Error innerError : error.getCauses()) {
                        causes.add(getAppError(statusCode, innerError));
                    }
                }
                return causes;
            }

            @Override
            public AppErrorException exception() {
                return new AppErrorException(this);
            }

            @Override
            public Error error() {
                return error;
            }

            @Override
            public String toString() {
                return "ClientProxyError" +
                        " { httpStatusCode=" + getHttpStatusCode() +
                        ", code=" + getCode() +
                        ", message=" + getDescription() +
                        ", field=" + getField() +
                        ", causes=" + getCauses() + " }";
            }
        };
    }
}
