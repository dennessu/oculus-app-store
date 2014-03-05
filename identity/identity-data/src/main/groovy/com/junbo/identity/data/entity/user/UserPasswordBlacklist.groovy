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
@Table(name = 'password_blacklist')
class UserPasswordBlacklist {
    @Id
    @Column(name = 'id')
    private Long key

    @Column(name = 'password')
    private String password

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

    String getPassword() {
        password
    }

    void setPassword(String password) {
        this.password = password
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
