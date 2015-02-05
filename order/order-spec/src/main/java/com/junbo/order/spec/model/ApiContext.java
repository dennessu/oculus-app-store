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
    public static final String HEADER_IP_GEO_LOCATION = "oculus-geoip-country-code";
    public static final String CLIENT_NAME = "x-client-name";
    public static final String CLIENT_VERSION = "x-client-version";
    public static final String PLATFORM_NAME = "x-platform-name";
    public static final String PLATFORM_VERSION = "x-platform-version";

    private String delegateUserId;
    private String requestorId;
    private String onBehalfOfRequestorId;
    private String userIp;
    private Boolean asyncCharge;
    private String location;
    private String clientName;
    private String clientVersion;
    private String platformName;
    private String platformVersion;

    public ApiContext() {

        setUserIp(JunboHttpContext.getRequestIpAddress());

        if (JunboHttpContext.getRequestHeaders()!= null &&
                !CollectionUtils.isEmpty(JunboHttpContext.getRequestHeaders().get(QA_HEADER_ASYNC_CHARGE))) {
            asyncCharge = Boolean.valueOf(JunboHttpContext.getRequestHeaders().getFirst(QA_HEADER_ASYNC_CHARGE));
        }
        if (JunboHttpContext.getRequestHeaders()!= null &&
                !CollectionUtils.isEmpty(JunboHttpContext.getRequestHeaders().get(HEADER_IP_GEO_LOCATION))) {
            location = JunboHttpContext.getRequestHeaders().getFirst(HEADER_IP_GEO_LOCATION);
        }
        if (JunboHttpContext.getRequestHeaders()!= null &&
                !CollectionUtils.isEmpty(JunboHttpContext.getRequestHeaders().get(CLIENT_NAME))) {
            setClientName(JunboHttpContext.getRequestHeaders().getFirst(CLIENT_NAME));
        }
        if (JunboHttpContext.getRequestHeaders()!= null &&
                !CollectionUtils.isEmpty(JunboHttpContext.getRequestHeaders().get(CLIENT_VERSION))) {
            setClientVersion(JunboHttpContext.getRequestHeaders().getFirst(CLIENT_VERSION));
        }
        if (JunboHttpContext.getRequestHeaders()!= null &&
                !CollectionUtils.isEmpty(JunboHttpContext.getRequestHeaders().get(PLATFORM_NAME))) {
            setPlatformName(JunboHttpContext.getRequestHeaders().getFirst(PLATFORM_NAME));
        }
        if (JunboHttpContext.getRequestHeaders()!= null &&
                !CollectionUtils.isEmpty(JunboHttpContext.getRequestHeaders().get(PLATFORM_VERSION))) {
            setPlatformVersion(JunboHttpContext.getRequestHeaders().getFirst(PLATFORM_VERSION));
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getPlatformVersion() {
        return platformVersion;
    }

    public void setPlatformVersion(String platformVersion) {
        this.platformVersion = platformVersion;
    }
}
