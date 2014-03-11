package com.junbo.order.mock

import com.junbo.catalog.spec.model.common.EntitiesGetOptions
import com.junbo.catalog.spec.model.common.EntityGetOptions
import com.junbo.catalog.spec.model.common.ResultList
import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.catalog.spec.resource.OfferResource
import com.junbo.common.id.Id
import com.junbo.langur.core.promise.Promise
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
* Created by LinYi on 14-2-25.
*/
@Component('mockCatalogResource')
@Scope('prototype')
class MockCatalogResource extends BaseMock implements OfferResource {

    @Override
    Promise<ResultList<Offer>> getOffers( EntitiesGetOptions options) {
        return null
    }

    @Override
    Promise<Offer> getOffer(Id offerId, EntityGetOptions options) {
        return generateOffer()
    }

    /**
     * Create a draft offer, the created offer is not purchasable until it is released.
     *
     * @param offer the offer to be created.
     * @return the created offer.
     */
    @Override
    Promise<Offer> create(Offer offer) {
        return null
    }

    @Override
    Promise<Offer> update(Id offerId, Offer offer) {
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
