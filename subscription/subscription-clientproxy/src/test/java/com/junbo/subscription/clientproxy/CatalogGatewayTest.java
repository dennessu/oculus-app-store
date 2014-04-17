/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.subscription.clientproxy;


import com.junbo.catalog.spec.model.common.LocalizableProperty;
import com.junbo.catalog.spec.model.common.Price;
import com.junbo.catalog.spec.model.common.Status;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemType;
import com.junbo.catalog.spec.model.offer.*;
import com.junbo.catalog.spec.resource.ItemResource;
import com.junbo.catalog.spec.resource.OfferResource;
import com.junbo.catalog.spec.resource.OfferRevisionResource;
import com.junbo.common.id.OfferRevisionId;
import junit.framework.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Catalog Gateway test.
 */

@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
public class CatalogGatewayTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private OfferResource offerResource;

    @Autowired
    private OfferRevisionResource offerRevisionResource;

    @Autowired
    private ItemResource itemResource;

    @Autowired
    private CatalogGateway catalogGateway;

    @Test(enabled = false)
    public void testBVT() {
        LocalizableProperty name = new LocalizableProperty();
        name.set("en_US", "subs_offer_name");

        Item item =  new Item();
        item.setOwnerId(123L);
        item.setType(ItemType.SUBSCRIPTION);
        item.setName(name);
        item.setSku("test_sku");

        final Long itemId = createItem(item);

        Offer offer = new Offer();
        offer.setOwnerId(123L);
        offer.setName(name);
        Long offerId = createOffer(offer);
        Assert.assertNotNull(offerId);

        OfferRevision offerRevision = new OfferRevision();
        offerRevision.setOfferId(offerId);
        offerRevision.setOwnerId(12345L);
        offerRevision.setStatus(Status.DRAFT);

        offerRevision.setItems(new ArrayList<ItemEntry>() {{
            add(new ItemEntry() {{
                setItemId(itemId);
                setQuantity(1);
            }
            });
        }}
        );

        Price price = new Price();
        price.setPriceType(Price.FREE);
        offerRevision.setPrice(price);

        Long offerRevisionId = createOfferRevision(offerRevision);
        Assert.assertNotNull(offerRevisionId);

        OfferRevision retrievedRevision = getOfferRevision(offerRevisionId);
        retrievedRevision.setStatus(Status.APPROVED);
        updateOfferRevision(retrievedRevision);

        Offer retrieved = catalogGateway.getOffer(offerId, System.currentTimeMillis());
        Assert.assertNotNull(retrieved);


    }

    public Long createItem(Item item) {
        try {
            return itemResource.create(item).wrapped().get().getItemId();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    public Long createOffer(Offer offer) {
        try {
            return offerResource.create(offer).wrapped().get().getOfferId();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    public Long createOfferRevision(OfferRevision offerRevision) {
        try {
            return offerRevisionResource.createOfferRevision(offerRevision).wrapped().get().getRevisionId();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    public OfferRevision getOfferRevision(Long offerRevisionId) {
        try {
            return offerRevisionResource.getOfferRevision(new OfferRevisionId(offerRevisionId)).wrapped().get();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    public Long updateOfferRevision(OfferRevision offerRevision) {
        try {
            return offerRevisionResource.updateOfferRevision(
                    new OfferRevisionId(offerRevision.getRevisionId()),
                    offerRevision).wrapped().get().getRevisionId();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during calling [Catalog] component service.", e);
        }
    }
}
