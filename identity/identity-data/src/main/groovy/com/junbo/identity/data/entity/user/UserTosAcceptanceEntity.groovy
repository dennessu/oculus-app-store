/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.user

import com.junbo.identity.data.entity.common.CommonStampEntity

import javax.persistence.*

/**
 * UserEntity model for user_device_profile table
 */
@Entity
@Table(name='user_tos_acceptance')
class UserTosAcceptanceEntity extends CommonStampEntity {
    @Id
    @Column(name = 'id')
    private Long id

    @Column(name = 'user_id')
    private Long userId

    @Column(name = 'tos_acceptance_url')
    private String tosAcceptanceUrl

    @Column(name = 'date_accepted')
    private Date dateAccepted

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

    String getTosAcceptanceUrl() {
        tosAcceptanceUrl
    }

    void setTosAcceptanceUrl(String tosAcceptanceUrl) {
        this.tosAcceptanceUrl = tosAcceptanceUrl
    }

    Date getDateAccepted() {
        dateAccepted
    }

    void setDateAccepted(Date dateAccepted) {
        this.dateAccepted = dateAccepted
    }
}