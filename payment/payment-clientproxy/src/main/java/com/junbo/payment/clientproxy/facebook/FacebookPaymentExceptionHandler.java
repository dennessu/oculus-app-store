/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.facebook;

import com.junbo.langur.core.client.ExceptionHandler;
import com.junbo.langur.core.client.MessageTranscoder;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppClientExceptions;
import com.junbo.payment.common.exception.AppServerExceptions;
import com.ning.http.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.IOException;

/**
 * Facebook Payment Exception Handler.
 */
public class FacebookPaymentExceptionHandler  implements ExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FacebookPaymentExceptionHandler.class);
    private MessageTranscoder messageTranscoder;

    @Required
    public void setMessageTranscoder(MessageTranscoder messageTranscoder) {
        this.messageTranscoder = messageTranscoder;
    }

    @Override
    public void handleExceptionResponse(Response response) {
        LOGGER.error("provider exception:", response);
        FacebookCCErrorDetail unknowError = new FacebookCCErrorDetail();
        unknowError.setCode("-1");
        unknowError.setMessage("unkonw error");
        unknowError.setType("provider");
        try {
            if(CommonUtil.isNullOrEmpty(response.getResponseBody())){
                throw new FacebookPaymentException(new FacebookPaymentError(unknowError));
            }
            FacebookPaymentError error = CommonUtil.parseJsonIgnoreUnknown(response.getResponseBody(), FacebookPaymentError.class);
            if(error != null && error.getError() !=null){
                if(FacebookErrorMapUtil.isClientError(error.getError())){
                    throw AppClientExceptions.INSTANCE.providerInvalidRequest("FacebookCC", error.getError().getMessage()).exception();
                }else{
                    throw AppServerExceptions.INSTANCE.providerProcessError("FacebookCC", error.getError().getMessage()).exception();
                }
            }else{
                throw AppServerExceptions.INSTANCE.providerProcessError("FacebookCC", response.getResponseBody()).exception();
            }
        } catch (IOException e) {
            LOGGER.error("provider exception handling:", e);
            throw new FacebookPaymentException(new FacebookPaymentError(unknowError));
        }
    }
}
