/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.user

import com.junbo.common.util.EnumRegistry
import com.junbo.identity.data.entity.common.CommonStampEntity
import com.junbo.sharding.annotations.SeedId

import javax.persistence.*
/**
 * UserEntity model for user_device_profile table
 */
@Entity
@Table(name = 'user_account')
class UserEntity extends CommonStampEntity {
    Long getId() {
        id
    }

    void setId(Long key) {
        this.id = key
    }

    String getUserName() {
        userName
    }

    void setUserName(String userName) {
        this.userName = userName
    }

    Short getStatus() {
        status
    }

    void setStatus(Short status) {
        this.status = status
    }

    String getPassword() {
        password
    }

    void setPassword(String password) {
        this.password = password
    }

    Short getPasswordStrength() {
        passwordStrength == null ? null : passwordStrength.id
    }

    void setPasswordStrength(Short passwordStrength) {
        this.passwordStrength = EnumRegistry.resolve(passwordStrength, UserPasswordStrength)
    }

    @Id
    @SeedId
    @Column(name = 'id')
    private Long id

    @Column(name = 'user_name')
    private String userName

    @Column(name = 'status')
    private Short status

    @Transient
    private String password

    @Transient
    private UserPasswordStrength passwordStrength
}
