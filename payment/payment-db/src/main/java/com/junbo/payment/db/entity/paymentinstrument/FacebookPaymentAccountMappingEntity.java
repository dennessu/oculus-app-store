/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.entity.paymentinstrument;

import com.junbo.payment.db.entity.GenericEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * payment instrument entity.
 */
@Entity
@Table(name = "facebook_payment_account_mapping")
public class FacebookPaymentAccountMappingEntity extends GenericEntity {
    @Id
    @Column(name = "map_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "fb_payment_account_id")
    private String fbPaymentAccountId;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getShardMasterId() {
        return userId;
    }

    public String getFbPaymentAccountId() {
        return fbPaymentAccountId;
    }

    public void setFbPaymentAccountId(String fbPaymentAccountId) {
        this.fbPaymentAccountId = fbPaymentAccountId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
