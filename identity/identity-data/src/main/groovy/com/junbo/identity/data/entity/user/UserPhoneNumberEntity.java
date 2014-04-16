/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by liangfu on 3/16/14.
 */
@Entity
@Table(name = "user_phone_number")
public class UserPhoneNumberEntity {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserPiiId() {
        return userPiiId;
    }

    public void setUserPiiId(Long userPiiId) {
        this.userPiiId = userPiiId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "user_pii_id")
    private Long userPiiId;
    @Column(name = "type")
    private String type;
    @Column(name = "value")
    private String value;
    @Column(name = "is_verified")
    private Boolean verified;
}
