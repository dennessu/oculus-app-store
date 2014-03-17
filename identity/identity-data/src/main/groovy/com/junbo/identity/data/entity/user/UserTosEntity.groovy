/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.user

import com.junbo.identity.data.entity.common.ResourceMetaEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * UserEntity model for user_device_profile table
 */
@Entity
@Table(name='user_tos')
class UserTosEntity extends ResourceMetaEntity {
    @Id
    @Column(name = 'id')
    private Long id

    @Column(name = 'user_id')
    private Long userId

    @Column(name = 'tos_uri')
    private String tosUri

    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    Long getUserId() {
        return userId
    }

    void setUserId(Long userId) {
        this.userId = userId
    }

    String getTosUri() {
        return tosUri
    }

    void setTosUri(String tosUri) {
        this.tosUri = tosUri
    }
}