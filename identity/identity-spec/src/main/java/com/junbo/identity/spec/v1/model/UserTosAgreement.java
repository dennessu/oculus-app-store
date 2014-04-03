/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.TosId;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTosAgreementId;
import com.junbo.common.util.Identifiable;
import com.junbo.identity.spec.model.users.ResourceMeta;

/**
 * Created by liangfu on 4/3/14.
 */
public class UserTosAgreement extends ResourceMeta implements Identifiable<UserTosAgreementId> {

    @JsonProperty("self")
    private UserTosAgreementId id;

    @JsonProperty("user")
    private UserId userId;

    @JsonProperty("tos")
    private TosId tosId;

    public UserTosAgreementId getId() {
        return id;
    }

    public void setId(UserTosAgreementId id) {
        this.id = id;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public TosId getTosId() {
        return tosId;
    }

    public void setTosId(TosId tosId) {
        this.tosId = tosId;
    }
}
