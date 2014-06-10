/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.GroupId;
import com.junbo.common.id.OrganizationId;
import com.junbo.common.id.UserId;
import com.junbo.common.jackson.annotation.HateoasLink;
import com.junbo.common.model.Link;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.junbo.common.util.Identifiable;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 4/3/14.
 */
public class Group extends PropertyAssignedAwareResourceMeta implements Identifiable<GroupId> {

    @ApiModelProperty(position = 1, required = true, value = "[Nullable]Link to this Group resource.")
    @JsonProperty("self")
    private GroupId id;

    @ApiModelProperty(position = 2, required = true, value = "The name of the group resource.")
    private String name;

    @ApiModelProperty(position = 3, required = false, value = "[Nullable] The status of the group; When do POST call, " +
            "the isActive maybe null if client doesn't provide so. When do GET call, the isActive will always have a value..")
    @JsonProperty("isActive")
    private Boolean active;

    @ApiModelProperty(position = 5, required = false, value = "[Client Immutable] UserGroupMembership resources in this group.")
    @HateoasLink("/user-group-memberships?groupId={id}")
    private Link userMemberships;

    @ApiModelProperty(position = 4, required = false, value = "[Client Immutable] Users in this group.")
    @HateoasLink("/users?groupId={id}")
    private Link users;

    @ApiModelProperty(position = 6, required = false, value = "Link to the Organization resource.")
    private OrganizationId owner;

    private UserId ownerUserId;

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

    public Link getUserMemberships() {
        return userMemberships;
    }

    public void setUserMemberships(Link userMemberships) {
        this.userMemberships = userMemberships;
        support.setPropertyAssigned("userMemberships");
    }

    public Link getUsers() {
        return users;
    }

    public void setUsers(Link users) {
        this.users = users;
        support.setPropertyAssigned("users");
    }

    public OrganizationId getOwner() {
        return owner;
    }

    public void setOwner(OrganizationId owner) {
        // todo:    Need to add logic check
        this.owner = owner;
        support.setPropertyAssigned("owner");
    }

    public UserId getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(UserId ownerUserId) {
        this.ownerUserId = ownerUserId;
    }
}
