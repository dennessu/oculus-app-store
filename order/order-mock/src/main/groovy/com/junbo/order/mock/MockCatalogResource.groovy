package com.junbo.order.mock

import com.junbo.catalog.spec.model.common.EntityGetOptions
import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.offer.Action
import com.junbo.catalog.spec.model.offer.Event
import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.catalog.spec.model.offer.OffersGetOptions
import com.junbo.catalog.spec.resource.OfferResource
import com.junbo.common.id.OfferId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.ws.rs.BeanParam
import javax.ws.rs.PathParam
import javax.ws.rs.core.Response

/**
* Created by LinYi on 14-2-25.
*/
@Component('mockCatalogResource')
@Scope('prototype')
class MockCatalogResource extends BaseMock implements OfferResource {

    /**
     * Create a draft offer, the created offer is not purchasable until it is released.
     *
     * @param offer the offer to be created.
     * @return the created offer.
     */
    @Override
    Promise<Results<Offer>> getOffers(@BeanParam OffersGetOptions options) {
        return null
    }

    @Override
    Promise<Offer> getOffer(@PathParam('offerId') OfferId offerId, @BeanParam EntityGetOptions options) {
        return generateOffer()
    }

    @Override
    Promise<Offer> create(Offer offer) {
        return null
    }

    @Override
    Promise<Offer> update(@PathParam('offerId') OfferId offerId, Offer offer) {
        return Promise.pure(null)
    }

    @Override
    Promise<Response> delete(@PathParam('offerId') OfferId offerId) {
        return null
    }

    Promise<Offer> generateOffer() {
        Offer offer = new Offer()
        offer.setName('fake_offer')
        offer.setOwnerId(generateLong())
        offer.setId(generateLong())
        offer.events = []
        def event = new Event()
        event.actions = []
        def action = new Action()
        action.type = BaseMock.GRANT_ENTITLEMENT
        event.actions.add(action)
        offer.events.add(event)
        Item offerItem = new Item()
        offerItem.type = 0L
        return Promise.pure(offer)
    }
}
