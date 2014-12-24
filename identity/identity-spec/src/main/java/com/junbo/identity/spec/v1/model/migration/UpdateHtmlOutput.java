/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model.migration;

import com.junbo.common.id.UserId;

/**
 * Created by xiali_000 on 2014/12/23.
 */
public class UpdateHtmlOutput{
    private UserId userId;
    private Boolean updated;

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public void setUpdated(Boolean updated) {
        this.updated = updated;
    }
}
