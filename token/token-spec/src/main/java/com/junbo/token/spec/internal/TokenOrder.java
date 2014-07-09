/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.spec.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.TokenOrderId;
import com.junbo.common.model.ResourceMetaForDualWrite;
import com.junbo.token.common.FilterOut;
import com.junbo.token.spec.model.TokenItem;

import java.util.Date;
import java.util.List;

/**
 * token order model.
 */
public class TokenOrder extends ResourceMetaForDualWrite<String> {
    @JsonProperty("self")
    @TokenOrderId
    private String id;
    private String description;
    private String tokenSetId;
    private String status;
    private Date expiredTime;
    private String usageLimit;
    private String createMethod;
    private Long quantity;
    private String activation;
    @FilterOut
    private Boolean encryptTokenString;
    private List<TokenItem> tokenItems;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTokenSetId() {
        return tokenSetId;
    }

    public void setTokenSetId(String tokenSetId) {
        this.tokenSetId = tokenSetId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
    }

    public String getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(String usageLimit) {
        this.usageLimit = usageLimit;
    }

    public String getCreateMethod() {
        return createMethod;
    }

    public void setCreateMethod(String createMethod) {
        this.createMethod = createMethod;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getActivation() {
        return activation;
    }

    public void setActivation(String activation) {
        this.activation = activation;
    }

    public List<TokenItem> getTokenItems() {
        return tokenItems;
    }

    public void setTokenItems(List<TokenItem> tokenItems) {
        this.tokenItems = tokenItems;
    }

    public Boolean getEncryptTokenString() {
        return encryptTokenString;
    }

    public void setEncryptTokenString(Boolean encryptTokenString) {
        this.encryptTokenString = encryptTokenString;
    }

}
