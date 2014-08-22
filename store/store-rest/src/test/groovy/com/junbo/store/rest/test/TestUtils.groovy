package com.junbo.store.rest.test
import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.catalog.spec.model.offer.OfferRevisionGetOptions
import com.junbo.catalog.spec.model.offer.OffersGetOptions
import com.junbo.catalog.spec.resource.OfferResource
import com.junbo.catalog.spec.resource.OfferRevisionResource
import com.junbo.common.id.OfferId
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.identity.spec.v1.resource.UserResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
/**
 * The TestUtils class.
 */
class TestUtils {

    private Map<String, OfferId> nameToOfferIds = null

    @Autowired(required = true)
    @Qualifier('offerClient')
    private OfferResource offerResource

    @Autowired(required = true)
    @Qualifier('offerRevisionClient')
    private OfferRevisionResource offerRevisionResource

    @Autowired(required = true)
    @Qualifier('storetest.userClient')
    private UserResource userResource

    @Autowired(required = true)
    @Qualifier('storetest.userPersonalInfoClient')
    private UserPersonalInfoResource userPersonalInfoResource

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
                    System.err.println("Duplicate offers with same name ${offerName}")
                }
                nameToOfferIds[offerName] = new OfferId(offer.getId())
            }
        }
        return nameToOfferIds[name]
    }

    public verifyUserEmail(UserId userId) {
        def user = userResource.get(userId, new UserGetOptions()).get()
        def defaultEmail = user.emails.find {UserPersonalInfoLink link -> link.isDefault}
        def pii = userPersonalInfoResource.get(defaultEmail.value, new UserPersonalInfoGetOptions()).get()
        pii.isValidated = true
        pii.lastValidateTime = new Date()
        userPersonalInfoResource.put(pii.getId(), pii)
    }

}
