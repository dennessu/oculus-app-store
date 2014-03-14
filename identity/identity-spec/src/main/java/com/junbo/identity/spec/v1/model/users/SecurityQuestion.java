/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model.users;

import com.junbo.common.id.SecurityQuestionId;
import com.junbo.common.util.Identifiable;

/**
 * Created by kg on 3/12/14.
 */
public class SecurityQuestion extends ResourceMeta implements Identifiable<SecurityQuestionId> {

    private SecurityQuestionId id;

    private String value;

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
}
