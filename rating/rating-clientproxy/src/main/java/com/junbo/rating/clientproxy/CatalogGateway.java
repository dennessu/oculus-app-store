/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.clientproxy;

import com.junbo.catalog.spec.model.promotion.Promotion;
import com.junbo.rating.spec.fusion.RatingOffer;

import java.util.List;

/**
 * Created by lizwu on 2/25/14.
 */
public interface CatalogGateway {
    RatingOffer getOffer(Long offerId);
    List<Promotion> getPromotions();

}
