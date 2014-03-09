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
    Promise<Offer> update(Offer offer) {
        return null
    }

    /**
     * Developer submit an draft offer for review.
     * @param offerId the id of the offer to be reviewed.
     * @return the offer to be reviewed.
     */
    @Override
    Promise<Offer> review(Id offerId) {
        return null
    }

    /**
     * Admin publishes an offer, makes it purchasable.
     * @param offerId the id of offer to be released.
     * @return the offer to be released.
     */
    @Override
    Promise<Offer> release(Id offerId) {
        return null
    }

    /**
     * Admin rejects an offer, developer may update and submit review later.
     * @param offerId the id of offer to be released.
     * @return the offer to be released.
     */
    @Override
    Promise<Offer> reject(Id offerId) {
        return null
    }

    /**
     * Remove an offer, makes it not purchasable. The draft version is still kept.
     * Developer may update and submit review again in future.
     * @param offerId the id of offer to be removed.
     * @return the removed offer id.
     */
    @Override
    Promise<Void> remove(Id offerId) {
        return null
    }

    /**
     * Delete an offer, delete both draft and released version.
     * Developer cannot operate this offer again in future.
     * @param offerId the id of offer to be deleted.
     * @return the deleted offer id.
     */
    @Override
    Promise<Void> delete(Id offerId) {
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
