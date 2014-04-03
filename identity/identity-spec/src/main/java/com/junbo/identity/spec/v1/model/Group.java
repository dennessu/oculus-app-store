/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.GroupId;
import com.junbo.common.util.Identifiable;
import com.junbo.identity.spec.model.users.ResourceMeta;

/**
 * Created by liangfu on 4/3/14.
 */
public class Group extends ResourceMeta implements Identifiable<GroupId> {

    @JsonProperty("self")
    private GroupId id;

    private String name;

    @Override
    public GroupId getId() {
        return id;
    }

    public void setId(GroupId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
