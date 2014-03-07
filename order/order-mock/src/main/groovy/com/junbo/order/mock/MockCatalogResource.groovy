package com.junbo.order.mock

import com.junbo.catalog.spec.model.common.EntitiesGetOptions
import com.junbo.catalog.spec.model.common.EntityGetOptions
import com.junbo.catalog.spec.model.common.ResultList
import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.catalog.spec.resource.OfferResource
import com.junbo.langur.core.promise.Promise
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.validation.Valid
import javax.ws.rs.PathParam
import javax.ws.rs.QueryParam

/**
* Created by LinYi on 14-2-25.
*/
@Component('mockCatalogResource')
@Scope('prototype')
class MockCatalogResource extends BaseMock implements OfferResource {

    @Override
    Promise<ResultList<Offer>> getOffers(
            @QueryParam('id') List<Long> ids, Integer start, Integer size) {
        return null
    }

    @Override
    Promise<Offer> getOffer(Long id) {
        return generateOffer()
    }

    @Override
    Promise<ResultList<Offer>> getOffers( EntitiesGetOptions options) {
        return null
    }

    @Override
    Promise<Offer> getOffer(Long offerId, EntityGetOptions options) {
        return generateOffer()
    }

    @Override
    Promise<Offer> createOffer(Offer offer) {
        return null
    }

    @Override
    Promise<Offer> updateOffer(@Valid Offer offer) {
        return null
    }

    @Override
    Promise<Offer> createReview(Long id) {
        return null
    }

    @Override
    Promise<Offer> releaseOffer(@PathParam('offerId') Long offerId) {
        return null
    }

    @Override
    Promise<Offer> publishOffer(Long id) {
        return null
    }

    @Override
    Promise<Offer> rejectOffer(@PathParam('offerId') Long offerId) {
        return null
    }

    @Override
    Promise<Long> removeOffer(@PathParam('offerId') Long offerId) {
        return null
    }

    @Override
    Promise<Long> deleteOffer(@PathParam('offerId') Long offerId) {
        return null
    }

    Promise<Offer> generateOffer() {
        Offer offer = new Offer()
        offer.setName('fake_offer')
        offer.setRevision(1)
        offer.setOwnerId(generateLong())
        offer.setId(generateLong())
        Item offerItem = new Item()
        offerItem.setType('PhyscialGoods')
        return Promise.pure(offer)
    }
}
