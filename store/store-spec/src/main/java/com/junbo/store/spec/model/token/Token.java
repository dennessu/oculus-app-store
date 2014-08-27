/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.token;

import com.junbo.common.id.UserId;
import com.junbo.common.model.ResourceMeta;

import java.util.Date;

/**
 * Created by liangfu on 8/27/14.
 */
public class Token extends ResourceMeta<String> {
    private String id;

    private UserId userId;

    // It should be PIN/PASSWORD
    private String type;

    private Date expireTime;

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
