package com.junbo.store.rest.test
import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.catalog.spec.model.offer.OfferRevisionGetOptions
import com.junbo.catalog.spec.model.offer.OffersGetOptions
import com.junbo.catalog.spec.resource.OfferResource
import com.junbo.catalog.spec.resource.OfferRevisionResource
import com.junbo.common.id.OfferId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
/**
 * The TestUtils class.
 */
class TestUtils {

    private Map<String, OfferId> nameToOfferIds = null

    @Autowired(required = true)
    @Qualifier('offerResourceClientProxy')
    private OfferResource offerResource

    @Autowired(required = true)
    @Qualifier('offerRevisionResourceClientProxy')
    private OfferRevisionResource offerRevisionResource

    public OfferId getByName(String name) {
        if (nameToOfferIds == null) {
            nameToOfferIds = new HashMap<>()
            def option = new OffersGetOptions(size: 100)
            List<Offer> offers = offerResource.getOffers(new OffersGetOptions(size: 100)).get().items
            assert offers.size() < 100
            offers.each { Offer offer ->
                OfferRevision offerRevision = offerRevisionResource.getOfferRevision(offer.currentRevisionId, new OfferRevisionGetOptions(locale: 'en_US')).get()
                String offerName = offerRevision.locales['en_US'].name
                if (nameToOfferIds.containsKey(offerName)) {
                    assert false, "Duplicate offers with same name ${offerName}"
                }
                nameToOfferIds[offerName] = new OfferId(offer.getId())
            }
        }
        return nameToOfferIds[name]
    }

}
