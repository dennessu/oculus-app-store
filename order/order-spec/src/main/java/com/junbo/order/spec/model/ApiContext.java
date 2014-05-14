/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import org.springframework.util.CollectionUtils;

import javax.ws.rs.core.MultivaluedMap;

/**
 * Created by chriszhu on 2/7/14.
 */
public class ApiContext {

    public static final String HEADER_DELEGATE_USER = "Delegate-User-Id";
    public static final String HEADER_REQUESTOR = "Requestor-Id";
    public static final String HEADER_ON_BEHALF_OF_REQUESTOR = "On-Behalf-Of-Requestor-Id";
    public static final String HEADER_USER_IP = "User_Ip";

    public static final String QA_HEADER_ASYNC_CHARGE = "X-QA-Async-Charge";

    private String delegateUserId;
    private String requestorId;
    private String onBehalfOfRequestorId;
    private String userIp;
    private Boolean asyncCharge;

    public ApiContext(MultivaluedMap<String, String> httpHeaders) {
        if (!CollectionUtils.isEmpty(httpHeaders.get(HEADER_USER_IP))) {
            setUserIp(httpHeaders.get(HEADER_USER_IP).get(0));
        }

        if (!CollectionUtils.isEmpty(httpHeaders.get(QA_HEADER_ASYNC_CHARGE))) {
            asyncCharge = Boolean.valueOf(httpHeaders.get(QA_HEADER_ASYNC_CHARGE).get(0));
        }
    }

    public String getDelegateUserId() {
        return delegateUserId;
    }

    public void setDelegateUserId(String delegateUserId) {
        this.delegateUserId = delegateUserId;
    }

    public String getRequestorId() {
        return requestorId;
    }

    public void setRequestorId(String requestorId) {
        this.requestorId = requestorId;
    }

    public String getOnBehalfOfRequestorId() {
        return onBehalfOfRequestorId;
    }

    public void setOnBehalfOfRequestorId(String onBehalfOfRequestorId) {
        this.onBehalfOfRequestorId = onBehalfOfRequestorId;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public Boolean getAsyncCharge() {
        return asyncCharge;
    }

    public void setAsyncCharge(Boolean asyncCharge) {
        this.asyncCharge = asyncCharge;
    }
}
