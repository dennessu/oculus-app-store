/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.user

import javax.persistence.*
/**
 * UserEntity model for user password entity table
 */
@Entity
@Table(name = 'user_password')
class UserPasswordEntity {
    @Id
    @Column(name = 'id')
    private Long key

    @Column(name = 'user_id')
    private Long userId

    @Column(name = 'password_hash')
    private String passwordHash

    @Column(name = 'password_salt')
    private String passwordSalt

    @Column(name = 'password_strength')
    private Short passwordStrength

    @Column(name = 'status')
    private Short status

    @Column(name = 'retry_failure_count')
    private int retryFailureCount

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = 'last_login_time')
    private Date lastLoginTime

    @Column(name = 'last_login_ip')
    private String lastLoginIp

    @Column(name = 'created_by')
    private String createdBy

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = 'created_time')
    private Date createdTime

    Long getKey() {
        key
    }

    void setKey(Long key) {
        this.key = key
    }

    Long getUserId() {
        userId
    }

    void setUserId(Long userId) {
        this.userId = userId
    }

    String getPasswordHash() {
        passwordHash
    }

    void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash
    }

    String getPasswordSalt() {
        passwordSalt
    }

    void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt
    }

    Short getPasswordStrength() {
        passwordStrength
    }

    void setPasswordStrength(Short passwordStrength) {
        this.passwordStrength = passwordStrength
    }

    Short getStatus() {
        status
    }

    void setStatus(Short status) {
        this.status = status
    }

    int getRetryFailureCount() {
        retryFailureCount
    }

    void setRetryFailureCount(int retryFailureCount) {
        this.retryFailureCount = retryFailureCount
    }

    Date getLastLoginTime() {
        lastLoginTime
    }

    void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime
    }

    String getLastLoginIp() {
        lastLoginIp
    }

    void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp
    }

    String getCreatedBy() {
        createdBy
    }

    void setCreatedBy(String createdBy) {
        this.createdBy = createdBy
    }

    Date getCreatedTime() {
        createdTime
    }

    void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime
    }
}
