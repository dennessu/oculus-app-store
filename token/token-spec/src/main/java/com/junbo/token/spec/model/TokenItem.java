/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.spec.model;

import com.junbo.token.common.FilterIn;
import com.junbo.token.common.FilterOut;

import java.util.List;

/**
 * Token item model.
 */
public class TokenItem {
    @FilterOut
    private Long id;
    @FilterOut
    private Long hashValue;
    private Long orderId;
    private String disableReason;
    @FilterIn
    private String status;
    private String encryptedString;
    @FilterIn
    private List<TokenConsumption> tokenConsumptions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHashValue() {
        return hashValue;
    }

    public void setHashValue(Long hashValue) {
        this.hashValue = hashValue;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getDisableReason() {
        return disableReason;
    }

    public void setDisableReason(String disableReason) {
        this.disableReason = disableReason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEncryptedString() {
        return encryptedString;
    }

    public void setEncryptedString(String encryptedString) {
        this.encryptedString = encryptedString;
    }

    public List<TokenConsumption> getTokenConsumptions() {
        return tokenConsumptions;
    }

    public void setTokenConsumptions(List<TokenConsumption> tokenConsumptions) {
        this.tokenConsumptions = tokenConsumptions;
    }
}
