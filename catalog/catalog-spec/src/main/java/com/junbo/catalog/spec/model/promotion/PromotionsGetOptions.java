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
 * Promotions get options.
 */
public class PromotionsGetOptions extends PageableGetOptions {
    @QueryParam("id")
    private List<String> promotionIds;

    public List<String> getPromotionIds() {
        return promotionIds;
    }

    public void setPromotionIds(List<String> promotionIds) {
        this.promotionIds = promotionIds;
    }
}
