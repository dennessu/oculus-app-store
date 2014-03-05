/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.user

import com.junbo.identity.data.entity.common.CommonStampEntity

import javax.persistence.*

/**
 * UserEntity model for user device profile table
 */
@Entity
@Table(name='user_device_profile')
class UserDeviceProfileEntity extends CommonStampEntity {

    @Id
    @Column(name = 'id')
    private Long id

    @Column(name = 'user_id')
    private Long userId

    @Column(name = 'type')
    private String type

    @Column(name = 'device_id')
    private Long deviceId

    @Column(name = 'last_updated_date')
    private Date lastUpdatedDate

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

    Date getLastUpdatedDate() {
        lastUpdatedDate
    }

    void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate
    }
}
