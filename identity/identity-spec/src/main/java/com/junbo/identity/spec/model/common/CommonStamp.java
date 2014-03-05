/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;
/**
 * Common stamp for identity to record createdDate/createdBy/updateDate/updatedBy.
 * Per resource will have those attributes.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonStamp {

    protected Date createdTime;

    protected Date updatedTime;

    protected Integer resourceAge;

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }


    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Integer getResourceAge() { return resourceAge; }

    public void setResourceAge(Integer resourceAge) { this.resourceAge = resourceAge; }
}
