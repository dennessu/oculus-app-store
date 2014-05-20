/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.clientproxy.mock;

import com.junbo.rating.spec.model.request.OfferRatingRequest;
import com.junbo.subscription.clientproxy.impl.RatingGatewayImpl;

/**
 * Created by Administrator on 14-5-20.
 */
public class MockRatingGatewayImpl extends RatingGatewayImpl{
    @Override
    public OfferRatingRequest offerRating(OfferRatingRequest request){
        return request;
    }
}
