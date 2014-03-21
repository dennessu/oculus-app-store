/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.util;

import com.junbo.common.error.AppErrorException;
import com.junbo.langur.core.client.ClientResponseException;
import com.junbo.payment.common.CommonUtil;


/**
 * proxy exception response.
 */
public class ProxyExceptionResponse {
    private static final String ERROR_CODE = "{\"result\": \"unknown error\"}";
    private int status;
    private String body;

    public int getStatus() {
        return status;
    }

    public String getBody() {
        if(CommonUtil.isNullOrEmpty(body)){
            return ERROR_CODE;
        }
        return body;
    }

    public ProxyExceptionResponse(Throwable throwable){
        if(throwable instanceof AppErrorException){
            AppErrorException appException = ((AppErrorException)throwable);
            try {
                status = appException.getError().getHttpStatusCode();
                body = CommonUtil.toJson(appException.getError(), null);
            }catch (Exception e) {

            }
        }else if(throwable instanceof ClientResponseException){
            try {
                status = ((ClientResponseException)throwable).getResponse().getStatusCode();
                body = ((ClientResponseException)throwable).getResponse().getResponseBody();
            } catch (Exception e) {

            }
        }
    }
}
