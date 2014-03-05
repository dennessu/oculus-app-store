/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.rating;

import com.junbo.langur.core.promise.Promise;
import com.junbo.order.spec.model.Order;
import com.junbo.rating.spec.model.request.OrderRatingRequest;

/**
 * Interface for rating facade.
 */
public interface RatingFacade {

    Promise<OrderRatingRequest> rateOrder(Order order, String shipToCountry);
}
