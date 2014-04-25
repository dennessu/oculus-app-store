/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.user;

import com.junbo.identity.data.entity.common.ResourceMetaEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * UserEntity list for user_device_profile table.
 */
@Entity
@Table(name = "user_tos")
public class UserTosAgreementEntity extends ResourceMetaEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "tos_id")
    private Long tosId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "agreement_time")
    private Date agreementTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTosId() {
        return tosId;
    }

    public void setTosId(Long tosId) {
        this.tosId = tosId;
    }

    public Date getAgreementTime() {
        return agreementTime;
    }

    public void setAgreementTime(Date agreementTime) {
        this.agreementTime = agreementTime;
    }
}
