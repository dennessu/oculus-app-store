/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.users;

import com.junbo.common.id.GroupId;
import com.junbo.common.util.Identifiable;

/**
 * Created by kg on 3/12/14.
 */
public class Group extends ResourceMeta implements Identifiable<GroupId> {

    private GroupId id;

    private String value;

    private Boolean active;

    // private List<UserGroup> users;

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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

}
