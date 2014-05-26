/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.clientproxy;

import com.junbo.rating.spec.model.subscription.SubsRatingRequest;

/**
 * Created by Administrator on 14-5-20.
 */
public interface RatingGateway {
    SubsRatingRequest subsRating(SubsRatingRequest request);
}
