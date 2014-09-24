/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.sentry;

import javax.ws.rs.QueryParam;

/**
 * Created by liangfu on 9/24/14.
 */
public class SentryQueryParam {
    @QueryParam("text_json")
    private String textJson;

    @QueryParam("other_json")
    private String otherJson;

    @QueryParam("ip")
    private String ipAddress;

    @QueryParam("format")
    private String format;

    @QueryParam("method")
    private String method;

    @QueryParam("access_token")
    private String accessToken;

    @QueryParam("namespace")
    private String namespace;

    @QueryParam("category")
    private String category;

    @QueryParam("source_id")
    private String sourceId;

    @QueryParam("target_id")
    private String targetId;

    public String getTextJson() {
        return textJson;
    }

    public void setTextJson(String textJson) {
        this.textJson = textJson;
    }

    public String getOtherJson() {
        return otherJson;
    }

    public void setOtherJson(String otherJson) {
        this.otherJson = otherJson;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }
}
