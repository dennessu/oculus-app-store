package com.junbo.order.mock
import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.catalog.spec.model.offer.OffersGetOptions
import com.junbo.catalog.spec.resource.OfferResource
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.ws.rs.BeanParam
import javax.ws.rs.PathParam
import javax.ws.rs.core.Response
/**
 * Created by chriszhu on 7/3/14.
 */


@CompileStatic
@Component('mockOfferResource')
@Scope('prototype')
class MockOfferResource extends BaseMock implements OfferResource {
    @Override
    Promise<Results<Offer>> getOffers(@BeanParam OffersGetOptions options) {
        return null
    }

    @Override
    Promise<Offer> getOffer(@PathParam("offerId") String offerId) {
        def offer = new Offer(
                offerId: offerId,
                currentRevisionId: offerId
        )
        return Promise.pure(offer)
    }

    @Override
    Promise<Offer> create(Offer offer) {
        return null
    }

    @Override
    Promise<Offer> update(@PathParam("offerId") String offerId, Offer offer) {
        return null
    }

    @Override
    Promise<Response> delete(@PathParam("offerId") String offerId) {
        return null
    }
}
