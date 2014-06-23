/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.rating;

import com.junbo.langur.core.promise.Promise;
import com.junbo.order.spec.model.Order;
import com.junbo.rating.spec.model.priceRating.RatingRequest;

/**
 * Interface for rating facade.
 */
public interface RatingFacade {

    Promise<RatingRequest> rateOrder(Order order);
}
