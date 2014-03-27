/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.subscription.clientproxy.mock;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.offer.ItemEntry;
import com.junbo.catalog.spec.model.offer.Offer;
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
    protected Item retrieveItem(Long itemId){
        Item mockItem = getSubsItem();
        if (mockItem == null) {
            throw new RuntimeException("item [" + itemId + "] not prepared in mock");
        }

        return mockItem;
    }

    private Offer getSubsOffer() {
        Offer offer = new Offer();
        offer.setId(1L);
        offer.setName("Subs Offer");
        offer.setPriceType("Free");

        offer.setItems(new ArrayList<ItemEntry>() {{
            add(new ItemEntry() {{
                setItemId(456L);
                setQuantity(1);
                setSku(null);
            }});
        }});

        return offer;
    }

    private Item getSubsItem() {
        return new Item() {{
            setId(456L);
            setType("SUBSCRIPTION");
        }};
    }
}
