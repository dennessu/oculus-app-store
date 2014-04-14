/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.model.users;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserOptinId;
import com.junbo.common.util.Identifiable;

/**
 * Created by kg on 3/10/14.
 */
public class UserOptin extends ResourceMeta implements Identifiable<UserOptinId> {

    private UserOptinId id;

    private String value;

    // Won't return
    private UserId userId;

    public UserOptinId getId() {
        return id;
    }

    public void setId(UserOptinId id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }
}
