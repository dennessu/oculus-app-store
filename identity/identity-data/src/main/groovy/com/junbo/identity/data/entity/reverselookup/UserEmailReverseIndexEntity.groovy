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
 * Created by liangfu on 3/21/14.
 */
@Entity
@Table(name = 'user_email_reverse_index')
@CompileStatic
class UserEmailReverseIndexEntity {
    @Id
    @SeedId
    @Column(name = 'value')
    private String value

    @Column(name = 'user_email_id')
    private Long userEmailId

    String getValue() {
        return value
    }

    void setValue(String value) {
        this.value = value
    }

    Long getUserEmailId() {
        return userEmailId
    }

    void setUserEmailId(Long userEmailId) {
        this.userEmailId = userEmailId
    }
}
