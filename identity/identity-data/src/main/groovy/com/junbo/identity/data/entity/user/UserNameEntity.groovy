/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.user

import com.junbo.sharding.annotations.SeedId
import groovy.transform.CompileStatic

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Created by liangfu on 3/17/14.
 */
@Entity
@Table(name = 'user_name')
@CompileStatic
class UserNameEntity {
    @Id
    @Column(name = 'id')
    private Long id

    @SeedId
    @Column(name = 'user_id')
    private Long userId

    @Column(name = 'first_name')
    private String firstName

    @Column(name = 'middle_name')
    private String middleName

    @Column(name = 'last_name')
    private String lastName

    @Column(name = 'honorific_prefix')
    private String honorificPrefix

    @Column(name = 'honorific_suffix')
    private String honorificSuffix

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

    String getFirstName() {
        return firstName
    }

    void setFirstName(String firstName) {
        this.firstName = firstName
    }

    String getMiddleName() {
        return middleName
    }

    void setMiddleName(String middleName) {
        this.middleName = middleName
    }

    String getLastName() {
        return lastName
    }

    void setLastName(String lastName) {
        this.lastName = lastName
    }

    String getHonorificPrefix() {
        return honorificPrefix
    }

    void setHonorificPrefix(String honorificPrefix) {
        this.honorificPrefix = honorificPrefix
    }

    String getHonorificSuffix() {
        return honorificSuffix
    }

    void setHonorificSuffix(String honorificSuffix) {
        this.honorificSuffix = honorificSuffix
    }
}
