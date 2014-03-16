/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.user

import com.junbo.identity.data.entity.common.CommonStampEntity
import com.junbo.sharding.annotations.SeedId

import javax.persistence.*

/**
 * UserFederationEntity model for user_device_profile table
 */
@Entity
@Table(name='user_federation')
class UserFederationEntity extends CommonStampEntity {
    @Id
    @Column(name = 'id')
    private Long id

    @SeedId
    @Column(name = 'user_id')
    private Long userId

    @Column(name = 'type')
    private String type

    @Column(name = 'value')
    private String value

    Long getId() {
        id
    }

    void setId(Long id) {
        this.id = id
    }

    Long getUserId() {
        userId
    }

    void setUserId(Long userId) {
        this.userId = userId
    }

    String getType() {
        type
    }

    void setType(String type) {
        this.type = type
    }

    String getValue() {
        value
    }

    void setValue(String value) {
        this.value = value
    }
}