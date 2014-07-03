/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.junbo.langur.core.context.JunboHttpContext;
import org.springframework.util.CollectionUtils;

/**
 * Created by chriszhu on 2/7/14.
 */
public class ApiContext {

    public static final String HEADER_DELEGATE_USER = "Delegate-User-Id";
    public static final String HEADER_REQUESTOR = "Requestor-Id";
    public static final String HEADER_ON_BEHALF_OF_REQUESTOR = "On-Behalf-Of-Requestor-Id";

    public static final String QA_HEADER_ASYNC_CHARGE = "X-QA-Async-Charge";

    private String delegateUserId;
    private String requestorId;
    private String onBehalfOfRequestorId;
    private String userIp;
    private Boolean asyncCharge;

    public ApiContext() {

        setUserIp(JunboHttpContext.getRequestIpAddress());

        if (JunboHttpContext.getRequestHeaders()!= null &&
                !CollectionUtils.isEmpty(JunboHttpContext.getRequestHeaders().get(QA_HEADER_ASYNC_CHARGE))) {
            asyncCharge = Boolean.valueOf(JunboHttpContext.getRequestHeaders().getFirst(QA_HEADER_ASYNC_CHARGE));
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
