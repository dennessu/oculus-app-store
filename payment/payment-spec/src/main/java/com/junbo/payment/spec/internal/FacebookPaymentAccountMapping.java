/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.internal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.model.ResourceMetaForDualWrite;

/**
 * Facebook Payment Account.
 */
public class FacebookPaymentAccountMapping extends ResourceMetaForDualWrite<Long> {
    private Long id;
    private Long userId;
    private String fbPaymentAccountId;

    public String getFbPaymentAccountId() {
        return fbPaymentAccountId;
    }

    public void setFbPaymentAccountId(String fbPaymentAccountId) {
        this.fbPaymentAccountId = fbPaymentAccountId;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }
    @JsonIgnore
    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
