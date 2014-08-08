/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.spec.internal;

import com.junbo.common.model.ResourceMetaForDualWrite;
import com.junbo.token.spec.model.ProductDetail;


/**
 * token set model.
 */
public class TokenSet extends ResourceMetaForDualWrite<String> {
    private String id;
    private String description;
    private String status;
    private String generationLength;
    private Long organizationId;
    private ProductDetail productDetail;
    private String productType;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGenerationLength() {
        return generationLength;
    }

    public void setGenerationLength(String generationLength) {
        this.generationLength = generationLength;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
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
}
