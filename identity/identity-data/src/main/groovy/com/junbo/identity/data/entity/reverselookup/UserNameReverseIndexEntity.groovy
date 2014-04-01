/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.reverselookup

import com.junbo.sharding.annotations.SeedId
import groovy.transform.CompileStatic

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Created by liangfu on 3/18/14.
 */
@Entity
@Table(name = 'user_name_reverse_index')
@CompileStatic
class UserNameReverseIndexEntity {

    @Id
    @SeedId
    @Column(name = 'user_name')
    private String username

    @Column(name = 'user_id')
    private Long userId

    String getUsername() {
        return username
    }

    void setUsername(String username) {
        this.username = username
    }

    Long getUserId() {
        return userId
    }

    void setUserId(Long userId) {
        this.userId = userId
    }
}
