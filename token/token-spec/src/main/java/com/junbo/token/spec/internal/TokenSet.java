/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.spec.internal;

import java.util.List;

/**
 * token set model.
 */
public class TokenSet {
    private Long id;
    private String description;
    private String status;
    private String generationLength;
    private Long generationSeed;
    private List<Long> offerIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getGenerationSeed() {
        return generationSeed;
    }

    public void setGenerationSeed(Long generationSeed) {
        this.generationSeed = generationSeed;
    }

    public List<Long> getOfferIds() {
        return offerIds;
    }

    public void setOfferIds(List<Long> offerIds) {
        this.offerIds = offerIds;
    }
}
