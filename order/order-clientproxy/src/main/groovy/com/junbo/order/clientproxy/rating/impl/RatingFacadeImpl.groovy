/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.rating.impl

import com.junbo.common.error.AppError
import com.junbo.common.error.AppErrorException
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.common.FacadeBuilder
import com.junbo.order.clientproxy.rating.RatingFacade
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.error.ErrorUtils
import com.junbo.order.spec.model.Order
import com.junbo.rating.spec.model.priceRating.RatingRequest
import com.junbo.rating.spec.resource.RatingResource
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import javax.annotation.Resource
/**
 * Rating facade implementation.
 */
@Component('orderRatingFacade')
@CompileStatic
@TypeChecked
class RatingFacadeImpl implements RatingFacade {

    @Resource(name = 'order.ratingClient')
    RatingResource ratingResource

    private static final Logger LOGGER = LoggerFactory.getLogger(RatingFacadeImpl)

    @Override
    Promise<RatingRequest> rateOrder(Order order) throws AppErrorException {
        LOGGER.info('name=RatingFacadeImpl_Rate_Order')
        RatingRequest request = FacadeBuilder.buildRatingRequest(order)
        return ratingResource.priceRating(request).recover { Throwable ex ->
            LOGGER.error('name=RatingFacadeImpl_Order_Rating_Error', ex)
            throw convertError(ex).exception()
        }.then { RatingRequest r ->
            if (r == null) {
                LOGGER.error('name=RatingFacadeImpl_Order_Rating_Null')
                throw AppErrors.INSTANCE.ratingResultInvalid('Rating response is null').exception()
            }
            LOGGER.info('name=RatingFacadeImpl_Rate_Order_Success')
            return Promise.pure(r)
        }
    }

    private AppError convertError(Throwable error) {
        AppError e = ErrorUtils.toAppError(error)
        if (e != null) {
            return AppErrors.INSTANCE.ratingResultError(e)
        }
        return AppErrors.INSTANCE.ratingConnectionError(error.message)
    }
}
