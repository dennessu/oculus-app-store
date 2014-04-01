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
@Table(name='user_email')
@CompileStatic
class UserEmailEntity extends ResourceMetaEntity {
    @Id
    @Column(name = 'id')
    private Long id

    @SeedId
    @Column(name = 'user_id')
    private Long userId

    @Column(name = 'value')
    private String value

    @Column(name = 'type')
    private String type

    @Column(name = 'is_primary')
    private Boolean primary

    @Column(name = 'is_verified')
    private Boolean verified

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

    String getValue() {
        return value
    }

    void setValue(String value) {
        this.value = value
    }

    String getType() {
        return type
    }

    void setType(String type) {
        this.type = type
    }

    Boolean getPrimary() {
        return primary
    }

    void setPrimary(Boolean primary) {
        this.primary = primary
    }

    Boolean getVerified() {
        return verified
    }

    void setVerified(Boolean verified) {
        this.verified = verified
    }
}
