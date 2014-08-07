/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.cloudant.json.annotations.CloudantIgnore;
import com.junbo.common.id.UserCredentialId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Created by kg on 4/3/14.
 */
public class RateUserCredentialRequest {

    private String type;

    private String value;

    private RateUserCredentialContext context;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public RateUserCredentialContext getContext() {
        return context;
    }

    public void setContext(RateUserCredentialContext context) {
        this.context = context;
    }
}
