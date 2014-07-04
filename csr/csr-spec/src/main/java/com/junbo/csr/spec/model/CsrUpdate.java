/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.csr.spec.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.CsrUpdateId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;

/**
 * Created by haomin on 14-7-4.
 */
public class CsrUpdate extends PropertyAssignedAwareResourceMeta<CsrUpdateId> {
    public CsrUpdateId getId() {
        return id;
    }

    public void setId(CsrUpdateId id) {
        this.id = id;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @JsonProperty("self")
    private CsrUpdateId id;
    private UserId userId;
    private String content;
    private Boolean isActive;
}
