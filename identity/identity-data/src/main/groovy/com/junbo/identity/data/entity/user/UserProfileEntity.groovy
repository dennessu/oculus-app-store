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
 * User Profile information
 */
@Entity
@Table(name='user_profile')
class UserProfileEntity extends ResourceMetaEntity {
    @Id
    @Column(name = 'id')
    private Long id

    @Column(name = 'user_id')
    private Long userId

    @Column(name = 'type')
    private Short type

    @Column(name = 'region')
    private String region

    @Column(name = 'locale')
    private String locale

    @Column(name = 'dob')
    private Date dob

    @Column(name = 'first_name')
    private String firstName

    @Column(name = 'middle_name')
    private String middleName

    @Column(name = 'last_name')
    private String lastName

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

    Short getType() {
        type
    }

    void setType(Short type) {
        this.type = type
    }

    String getRegion() {
        region
    }

    void setRegion(String region) {
        this.region = region
    }

    String getLocale() {
        locale
    }

    void setLocale(String locale) {
        this.locale = locale
    }

    Date getDob() {
        dob
    }

    void setDob(Date dob) {
        this.dob = dob
    }

    String getFirstName() {
        firstName
    }

    void setFirstName(String firstName) {
        this.firstName = firstName
    }

    String getMiddleName() {
        middleName
    }

    void setMiddleName(String middleName) {
        this.middleName = middleName
    }

    String getLastName() {
        lastName
    }

    void setLastName(String lastName) {
        this.lastName = lastName
    }
}
