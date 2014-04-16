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
 * Created by liangfu on 4/16/14.
 */
@Entity
@Table(name = "user_address")
public class UserAddressEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "user_pii_id")
    private Long userPiiId;

    @Column(name = "address_id")
    private Long addressId;

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

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }
}
