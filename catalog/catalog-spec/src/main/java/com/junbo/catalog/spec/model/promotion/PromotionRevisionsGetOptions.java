/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.promotion;

import com.junbo.catalog.spec.model.common.PageableGetOptions;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Promotion revisions get options.
 */
public class PromotionRevisionsGetOptions extends PageableGetOptions {
    @QueryParam("promotionId")
    private List<String> promotionIds;
    @QueryParam("revisionId")
    private List<String> revisionIds;
    @QueryParam("status")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getPromotionIds() {
        return promotionIds;
    }

    public void setPromotionIds(List<String> promotionIds) {
        this.promotionIds = promotionIds;
    }

    public List<String> getRevisionIds() {
        return revisionIds;
    }

    public void setRevisionIds(List<String> revisionIds) {
        this.revisionIds = revisionIds;
    }
}
