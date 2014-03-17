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
import com.junbo.sharding.annotations.SeedId

/**
 * UserEntity model for user device profile table
 */
@Entity
@Table(name='user_device')
class UserDeviceEntity extends ResourceMetaEntity {

    @Id
    @Column(name = 'id')
    private Long id

    @SeedId
    @Column(name = 'user_id')
    private Long userId

    @Column(name = 'type')
    private String type

    @Column(name = 'device_id')
    private Long deviceId

    @Column(name = 'os')
    private String os

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

    Long getDeviceId() {
        deviceId
    }

    void setDeviceId(Long deviceId) {
        this.deviceId = deviceId
    }

    String getOs() {
        return os
    }

    void setOs(String os) {
        this.os = os
    }
}
