/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.TosId;

import java.util.Date;

/**
 * The Tos class.
 */
public class Tos {

    @JsonProperty("self")
    private TosId tosId;

    private String type;

    private String version;

    private String title;

    private String content;

    private String landingUrl;

    private Boolean accepted;

    private Date acceptedDate;

    public TosId getTosId() {
        return tosId;
    }

    public void setTosId(TosId tosId) {
        this.tosId = tosId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLandingUrl() {
        return landingUrl;
    }

    public void setLandingUrl(String landingUrl) {
        this.landingUrl = landingUrl;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public Date getAcceptedDate() {
        return acceptedDate;
    }

    public void setAcceptedDate(Date acceptedDate) {
        this.acceptedDate = acceptedDate;
    }
}
