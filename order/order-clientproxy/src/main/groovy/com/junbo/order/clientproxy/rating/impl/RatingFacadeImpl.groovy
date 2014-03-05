/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.rating.impl
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.common.FacadeBuilder
import com.junbo.order.clientproxy.rating.RatingFacade
import com.junbo.order.spec.model.Order
import com.junbo.rating.spec.model.request.OrderRatingRequest
import com.junbo.rating.spec.resource.RatingResource
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Rating facade implementation.
 */
@Component('ratingFacade')
@CompileStatic
@TypeChecked
class RatingFacadeImpl implements RatingFacade {

    @Resource(name='ratingClient')
    RatingResource ratingResource

    @Override
    Promise<OrderRatingRequest> rateOrder(Order order, String shipToCountry) {
        OrderRatingRequest request = FacadeBuilder.buildOrderRatingRequest(order, shipToCountry)
        return ratingResource.orderRating(request)
    }
}
