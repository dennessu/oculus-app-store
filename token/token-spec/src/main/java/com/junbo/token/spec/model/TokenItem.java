/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.spec.model;

import com.junbo.common.jackson.annotation.TokenOrderId;
import com.junbo.common.model.ResourceMetaForDualWrite;
import com.junbo.token.common.FilterIn;
import com.junbo.token.common.FilterOut;

import java.util.List;

/**
 * Token item model.
 */
public class TokenItem extends ResourceMetaForDualWrite<String> {
    @FilterOut
    private String id;
    @FilterOut
    private Long hashValue;
    @TokenOrderId
    private String orderId;
    private String disableReason;
    @FilterIn
    private String status;
    private String encryptedString;
    @FilterIn
    private List<TokenConsumption> tokenConsumptions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getHashValue() {
        return hashValue;
    }

    public void setHashValue(Long hashValue) {
        this.hashValue = hashValue;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
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
