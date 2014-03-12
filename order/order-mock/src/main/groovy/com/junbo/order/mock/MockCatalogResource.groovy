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
    Promise<Offer> getOffer(Long offerId, EntityGetOptions options) {
        return generateOffer()
    }

    @Override
    Promise<Offer> update(Long offerId, Offer offer) {
        return Promise.pure(null)
    }

    Promise<Offer> generateOffer() {
        Offer offer = new Offer()
        offer.setName('fake_offer')
        offer.setRevision(1)
        offer.setOwnerId(generateLong())
        offer.setId(generateLong())
        Item offerItem = new Item()
        offerItem.type = 0L
        return Promise.pure(offer)
    }


}
