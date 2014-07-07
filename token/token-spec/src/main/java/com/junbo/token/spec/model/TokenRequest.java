/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.spec.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.TokenOrderId;
import com.junbo.token.common.FilterIn;
import com.junbo.token.common.InnerFilter;

import java.util.Date;
import java.util.List;

/**
 * order request from developer.
 */
public class TokenRequest {
    @JsonProperty("self")
    @TokenOrderId
    private String id;
    private String description;
    private String generationLength;
    private String productType;
    private ProductDetail productDetail;
    @FilterIn
    private String status;
    private Date expiredTime;
    private String usageLimit;
    private String createMethod;
    private Long quantity;
    private String activation;
    @InnerFilter
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

    public String getGenerationLength() {
        return generationLength;
    }

    public void setGenerationLength(String generationLength) {
        this.generationLength = generationLength;
    }

    public ProductDetail getProductDetail() {
        return productDetail;
    }

    public void setProductDetail(ProductDetail productDetail) {
        this.productDetail = productDetail;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
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

}
