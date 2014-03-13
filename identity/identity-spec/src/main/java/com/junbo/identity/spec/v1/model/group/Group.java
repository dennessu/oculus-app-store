/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model.group;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.GroupId;

/**
 * Created by kg on 3/12/14.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Group {

    @JsonProperty("self")
    private GroupId id;

    private String value;

    private String displayName;

    private Boolean active;

    public GroupId getId() {
        return id;
    }

    public void setId(GroupId id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
