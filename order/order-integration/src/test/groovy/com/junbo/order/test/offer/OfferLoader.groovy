package com.junbo.order.test.offer
import com.junbo.catalog.spec.model.common.LocalizableProperty
import com.junbo.catalog.spec.model.common.Price
import com.junbo.catalog.spec.model.common.Status
import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.item.ItemRevision
import com.junbo.catalog.spec.model.offer.*
import com.junbo.common.id.ItemRevisionId
import com.junbo.common.id.OfferRevisionId
import com.junbo.fulfilment.common.util.Constant
import com.junbo.order.test.ServiceFacade
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.stereotype.Component
/**
 * Created by fzhang on 14-3-19.
 */
@Component('offerLoader')
class OfferLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfferLoader)

    @Autowired
    def ServiceFacade serviceFacade

    public static void main(String[] args) {
        LOGGER.info('Start load offers')
        ApplicationContext context =
                new ClassPathXmlApplicationContext("classpath:spring/context-test.xml")
        def loader = context.getBean(OfferLoader)
        loader.loadOffers()
        LOGGER.info('Finish load offers')
        System.exit(0)
    }

    void loadOffers() {
        def owner = serviceFacade.postUser().id
        def itemRevision = createItemRevision(createItem(owner.value))
        createOfferRevision(createOffer(owner.value), itemRevision.itemId)
    }

    Item createItem(Long ownerId) {
        def item = new Item()
        item.name = new LocalizableProperty()
        item.name.locales = ['en_US': 'en_US_test_item_name', 'DEFAULT': 'default_test_item_name']
        item.ownerId = ownerId
        item.sku = 'sku'
        item.type = 'DIGITAL'
        return serviceFacade.itemResource.create(item).wrapped().get()
    }

    ItemRevision createItemRevision(Item item){
        def itemRevision = new ItemRevision()
        itemRevision.type = item.type
        itemRevision.sku = item.sku
        itemRevision.ownerId = item.ownerId
        itemRevision.itemId = item.itemId
        itemRevision.status = Status.DRAFT
        ItemRevision draft = serviceFacade.itemRevisionResource.createItemRevision(itemRevision).wrapped().get()
        draft.status = Status.APPROVED
        return serviceFacade.itemRevisionResource.updateItemRevision(new ItemRevisionId(draft.revisionId), draft).wrapped().get()
    }

    Offer createOffer(Long ownerId){
        def offer = new Offer()
        offer.ownerId = ownerId
        return serviceFacade.offerResource.create(offer).wrapped().get()
    }

    OfferRevision createOfferRevision(Offer offer, Long itemId) {
        OfferRevision offerRevision = new OfferRevision();
        offerRevision.offerId = offer.offerId
        offerRevision.ownerId = offer.ownerId
        offerRevision.status = Status.DRAFT;
        offerRevision.name = new LocalizableProperty()
        offerRevision.name.locales = ["en_US": "en_US_test_item_name", "DEFAULT": "default_test_item_name"]

        offerRevision.price = new Price();
        offerRevision.price.priceType = Price.CUSTOM;
        offerRevision.price.prices = ['USD': 9.99G, 'EUR': 7.99]
        offerRevision.events = ['PURCHASE': new Event(
                name: Constant.EVENT_PURCHASE,
                actions: [new Action(
                        type: Constant.ACTION_GRANT_ENTITLEMENT,
                        properties: ["ENTITLEMENT_DEF_ID": "12345"]
                )]
        )]
        offerRevision.items = [new ItemEntry(
                itemId: itemId,
                quantity: 1
        )]
        OfferRevision draft = serviceFacade.offerRevisionResource.createOfferRevision(offerRevision).wrapped().get()
        draft.status = Status.APPROVED
        return serviceFacade.offerRevisionResource.updateOfferRevision(new OfferRevisionId(draft.revisionId), draft).wrapped().get()
    }
}
