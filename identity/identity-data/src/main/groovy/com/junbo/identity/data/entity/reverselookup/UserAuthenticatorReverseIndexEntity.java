/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.reverselookup;

import com.junbo.sharding.annotations.SeedId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by liangfu on 3/21/14.
 */
@Entity
@Table(name = "user_authenticator_reverse_index")
public class UserAuthenticatorReverseIndexEntity {
    @Id
    @SeedId
    @Column(name = "value")
    private String value;

    @Column(name = "authenticator_id")
    private Long userAuthenticatorId;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getUserAuthenticatorId() {
        return userAuthenticatorId;
    }

    public void setUserAuthenticatorId(Long userAuthenticatorId) {
        this.userAuthenticatorId = userAuthenticatorId;
    }
}
