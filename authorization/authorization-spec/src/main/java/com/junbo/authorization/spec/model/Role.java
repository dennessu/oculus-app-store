/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.spec.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.RoleId;
import com.junbo.common.model.ResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Role.
 */
public class Role extends ResourceMeta<RoleId> {

    @ApiModelProperty(position = 1, required = true, value = "[Nullable]The id of the role resource.")
    @JsonProperty("self")
    private RoleId id;

    @ApiModelProperty(position = 2, required = true, value = "The name of the role resource, must be Admin, Developer, Publisher.")
    private String name;

    @ApiModelProperty(position = 3, required = true, value = "The role target.")
    private RoleTarget target;

    @Override
    public RoleId getId() {
        return id;
    }

    public void setId(RoleId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RoleTarget getTarget() {
        return target;
    }

    public void setTarget(RoleTarget target) {
        this.target = target;
    }
}
