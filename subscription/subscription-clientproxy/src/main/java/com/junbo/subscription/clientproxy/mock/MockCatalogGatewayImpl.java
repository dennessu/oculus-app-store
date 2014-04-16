/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.subscription.clientproxy.mock;

import com.junbo.catalog.spec.model.common.Price;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.offer.ItemEntry;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.subscription.clientproxy.impl.CatalogGatewayImpl;

import java.util.ArrayList;

/**
 * Created by Administrator on 14-3-27.
 */
public class MockCatalogGatewayImpl extends CatalogGatewayImpl {

    @Override
    protected Offer retrieveOffer(Long offerId, Long timestamp){
        Offer mockOffer = getSubsOffer();
        if (mockOffer == null) {
            throw new RuntimeException("offer [" + offerId + "] not prepared in mock");
        }

        return mockOffer;
    }
    @Override
    protected OfferRevision retrieveOfferRev(Long offerRevId){
        OfferRevision mockOfferRev = getSubsOfferRev();
        if (mockOfferRev == null) {
            throw new RuntimeException("offer [" + mockOfferRev + "] not prepared in mock");
        }

        return mockOfferRev;
    }


    @Override
    protected Item retrieveItem(Long itemId){
        Item mockItem = getSubsItem();
        if (mockItem == null) {
            throw new RuntimeException("item [" + itemId + "] not prepared in mock");
        }

        return mockItem;
    }

    private Offer getSubsOffer() {
        Offer offer = new Offer();
        offer.setOfferId(1L);
        //offer.setName("Subs Offer");
        //offer.setPriceType("Free");

        offer.setCurrentRevisionId(2L);

        return offer;
    }

    private OfferRevision getSubsOfferRev() {
        OfferRevision offerRev = new OfferRevision();
        offerRev.setOfferId(1L);
        offerRev.setRevisionId(2L);
        //offerRev.setName("Subs Offer");
        Price price = new Price();
        price.setPriceType(Price.FREE);
        offerRev.setPrice(price);

        offerRev.setItems(new ArrayList<ItemEntry>() {{
            add(new ItemEntry() {{
                setItemId(456L);
                setQuantity(1);
            }});
        }});

        return offerRev;
    }

    private Item getSubsItem() {
        return new Item() {{
            setItemId(456L);
            setType("SUBSCRIPTION");
        }};
    }
}
