package com.junbo.order.test.offer

import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.offer.*
import com.junbo.common.id.ItemId
import com.junbo.common.id.OfferId
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
        createOffer(
            new Offer(
                    name: '3D Parking Simulator',
                    type: 12,
                    ownerId: owner.value,
                    status: 'Design',
                    categories: [123],
                    prices: [US:new Price(amount: 9.99, currency: 'USD')],
                    subOffers: [],
                    events: [
                            new Event
                            (
                                name: 'PURCHASE_EVENT',
                                actions: [
                                    new Action (
                                        type: 'GRANT_ENTITLEMENT',
                                        properties: [
                                            tag: 'item001_ANGRY.BIRD_ONLINE_ACCESS',
                                            group: 'Angry Bird',
                                            type: 'ONLINE_ACCESS',
                                            duration: '3Month'
                                        ]
                                    )
                                ]
                            )
                    ],
                    localeProperties: [
                        DEFAULT: [
                            description: '3D Parking Simulator is a VR driving simulator specialized for parking.'
                        ]
                    ],
                    properties: [
                        mainImage: 'the img url'
                    ]
            ),
            [
                    new Item(
                        name: 'Angry Bird',
                        type: 123,
                        status: 'Design',
                        ownerId: owner.value,
                        skus:[],
                        properties: [
                            gameModes: 'Single-Player',
                            platforms: 'Mac, Windows, Linux',
                            minSystemRequirements: 'Windows 7 or later (64 bit)',
                            description: '3D Parking Simulator is a VR driving simulator specialized for parking..'
                        ]

                    )
            ]
        )
    }

    Offer createOffer(Offer offer, List<Item> items) {
        List<Item> result = []
        items.each {
            def item = serviceFacade.itemResource.create(it).wrapped().get()
            //item.status = 'RELEASED'
            //result << serviceFacade.itemResource.update(new ItemId(item.id), item)
        }
        offer.items = []
        result.each {
            offer.items << new ItemEntry(
                    itemId: it.id
            )
        }
        offer = serviceFacade.offerResource.create(offer).wrapped().get()
        offer.status = 'RELEASED'
        return serviceFacade.offerResource.update(new OfferId(offer.id), offer).wrapped().get()
    }
}
