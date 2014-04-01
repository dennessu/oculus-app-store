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
 * Created by liangfu on 3/16/14.
 */
@Entity
@Table(name = 'user_login_attempt')
@CompileStatic
class UserLoginAttemptEntity extends ResourceMetaEntity {
    @Id
    @Column(name = 'id')
    private Long id

    @SeedId
    @Column(name = 'user_id')
    private Long userId

    @Column(name = 'type')
    private String type

    @Column(name = 'ip_address')
    private String ipAddress

    @Column(name = 'user_agent')
    private String userAgent

    @Column(name = 'client_id')
    private String clientId

    @Column(name = 'succeeded')
    private Boolean succeeded

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

    String getIpAddress() {
        return ipAddress
    }

    void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress
    }

    String getUserAgent() {
        return userAgent
    }

    void setUserAgent(String userAgent) {
        this.userAgent = userAgent
    }

    String getClientId() {
        return clientId
    }

    void setClientId(String clientId) {
        this.clientId = clientId
    }

    Boolean getSucceeded() {
        return succeeded
    }

    void setSucceeded(Boolean succeeded) {
        this.succeeded = succeeded
    }
}
