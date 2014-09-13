package com.junbo.store.clientproxy.rating

import com.junbo.common.id.OfferId
import com.junbo.langur.core.promise.Promise
import com.junbo.rating.spec.model.priceRating.RatingItem
import com.junbo.rating.spec.model.priceRating.RatingRequest
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.spec.model.ApiContext
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import javax.annotation.Resource
/**
 * The PriceRatingFacadeImpl class.
 */
@CompileStatic
@Component('storePriceRatingFacade')
class PriceRatingFacadeImpl implements PriceRatingFacade {

    private final static Logger LOGGER = LoggerFactory.getLogger(PriceRatingFacadeImpl)

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Override
    Promise<RatingItem> rateOffer(OfferId offerId,  ApiContext apiContext) {
        resourceContainer.ratingResource.priceRating(new RatingRequest(
                    includeCrossOfferPromos: false,
                    country: apiContext.country.getId().value,
                    currency: apiContext.currency.getId().value,
                    lineItems: [
                            new RatingItem(
                                    offerId: offerId.value,
                                    quantity: 1
                            )
                    ] as Set)).recover { Throwable ex ->
            LOGGER.error('name=Store_Price_Rating_Fail, offer={}', offerId, ex)
            return Promise.pure()
        }.then { RatingRequest ratingResult ->
            return Promise.pure(RatingItem)
        }
    }
}
