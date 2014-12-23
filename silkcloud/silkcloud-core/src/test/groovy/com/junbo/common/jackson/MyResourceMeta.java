/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;

/**
 * TestEntity.
 */
public class MyResourceMeta extends PropertyAssignedAwareResourceMeta<UserId> {

    @JsonProperty("self")
    private com.junbo.common.id.UserId id;

    private String username;

    public UserId getId() {
        return id;
    }

    public void setId(UserId id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
