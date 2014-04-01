/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.user

import com.junbo.identity.data.entity.common.ResourceMetaEntity
import com.junbo.sharding.annotations.SeedId
import groovy.transform.CompileStatic

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * UserEntity model for user device profile table.
 */
@Entity
@Table(name = 'user_device')
@CompileStatic
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
    private String deviceId

    @Column(name = 'os')
    private String os

    @Column(name = 'name')
    private String name

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

    String getType() {
        return type
    }

    void setType(String type) {
        this.type = type
    }

    String getDeviceId() {
        return deviceId
    }

    void setDeviceId(String deviceId) {
        this.deviceId = deviceId
    }

    String getOs() {
        return os
    }

    void setOs(String os) {
        this.os = os
    }

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

}

