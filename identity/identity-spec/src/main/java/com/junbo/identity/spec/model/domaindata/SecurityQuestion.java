/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.domaindata;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.SecurityQuestionId;
import com.junbo.common.util.Identifiable;
import com.junbo.identity.spec.model.users.ResourceMeta;

/**
 * Created by kg on 3/12/14.
 */
public class SecurityQuestion extends ResourceMeta implements Identifiable<SecurityQuestionId> {
    @JsonProperty("self")
    private SecurityQuestionId id;

    private String value;

    private Boolean active;

    public SecurityQuestionId getId() {
        return id;
    }

    public void setId(SecurityQuestionId id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
