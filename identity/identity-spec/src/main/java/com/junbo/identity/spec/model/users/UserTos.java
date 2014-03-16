/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.users;

import com.junbo.common.id.UserTosId;
import com.junbo.common.util.Identifiable;

/**
 * Created by kg on 3/10/14.
 */
public class UserTos extends ResourceMeta implements Identifiable<UserTosId> {

    private UserTosId id;

    private String tosUri;

    public UserTosId getId() {
        return id;
    }

    public void setId(UserTosId id) {
        this.id = id;
    }

    public String getTosUri() {
        return tosUri;
    }

    public void setTosUri(String tosUri) {
        this.tosUri = tosUri;
    }
}
