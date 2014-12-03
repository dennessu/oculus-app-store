/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.facebook;

import com.junbo.langur.core.client.ExceptionHandler;
import com.junbo.langur.core.client.MessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.payment.common.CommonUtil;
import com.ning.http.client.Response;
import org.springframework.beans.factory.annotation.Required;

import java.io.IOException;

/**
 * Facebook Payment Exception Handler.
 */
public class FacebookPaymentExceptionHandler  implements ExceptionHandler {
    private static final FacebookPaymentError UNEXPECTED_ERROR =
            new FacebookPaymentError(new FacebookPaymentError.Error("Unexpected error", null, 0));
    private MessageTranscoder messageTranscoder;

    @Required
    public void setMessageTranscoder(MessageTranscoder messageTranscoder) {
        this.messageTranscoder = messageTranscoder;
    }

    @Override
    public void handleExceptionResponse(Response response) {
        try {
            if(CommonUtil.isNullOrEmpty(response.getResponseBody())){
                throw new FacebookPaymentException(new FacebookPaymentError(new FacebookPaymentError.Error("empty response", null, 0)));
            }
            FacebookPaymentError error = (FacebookPaymentError)messageTranscoder.<FacebookPaymentError>decode(
                    new TypeReference<FacebookPaymentError>() { },response.getResponseBody());
            throw new FacebookPaymentException(error);
        } catch (IOException e) {
            throw new FacebookPaymentException(UNEXPECTED_ERROR, e);
        }
    }
}
