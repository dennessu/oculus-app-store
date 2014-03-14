/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model.domainData;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by kg on 3/12/14.
 */
public class SecurityQuestion {

    @JsonProperty("self")
    private SecurityQuestion id;

    private String value;

    public SecurityQuestion getId() {
        return id;
    }

    public void setId(SecurityQuestion id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

