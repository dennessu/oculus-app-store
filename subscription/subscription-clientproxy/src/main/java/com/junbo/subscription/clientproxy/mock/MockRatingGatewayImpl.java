/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.clientproxy.mock;

import com.junbo.rating.spec.model.subscription.SubsRatingRequest;
import com.junbo.subscription.clientproxy.impl.RatingGatewayImpl;

import java.math.BigDecimal;

/**
 * Created by Administrator on 14-5-20.
 */
public class MockRatingGatewayImpl extends RatingGatewayImpl{
    @Override
    public SubsRatingRequest subsRating(SubsRatingRequest request){
        request.setAmount(new BigDecimal(1.99));
        return request;
    }
}
