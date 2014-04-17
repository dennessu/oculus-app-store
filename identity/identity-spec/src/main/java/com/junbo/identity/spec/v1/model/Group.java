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
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 4/3/14.
 */
public class Group extends ResourceMeta implements Identifiable<GroupId> {

    @ApiModelProperty(position = 1, required = true, value = "The id of the group resource.")
    @JsonProperty("self")
    private GroupId id;

    @ApiModelProperty(position = 2, required = true, value = "The name of the group resource.")
    private String name;

    @ApiModelProperty(position = 3, required = false, value = "The status of the group.")
    @JsonProperty("isActive")
    private Boolean active;

    @Override
    public GroupId getId() {
        return id;
    }

    public void setId(GroupId id) {
        this.id = id;
        support.setPropertyAssigned("self");
        support.setPropertyAssigned("id");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        support.setPropertyAssigned("name");
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
        support.setPropertyAssigned("active");
        support.setPropertyAssigned("isActive");
    }
}
