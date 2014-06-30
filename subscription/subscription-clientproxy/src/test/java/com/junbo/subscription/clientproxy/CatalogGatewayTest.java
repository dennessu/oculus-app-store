/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.subscription.clientproxy;


import com.junbo.catalog.spec.enums.PriceType;
import com.junbo.catalog.spec.model.common.Price;
import com.junbo.catalog.spec.enums.Status;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.enums.ItemType;
import com.junbo.catalog.spec.model.offer.*;
import com.junbo.catalog.spec.resource.ItemResource;
import com.junbo.catalog.spec.resource.OfferResource;
import com.junbo.catalog.spec.resource.OfferRevisionResource;
import com.junbo.common.id.OfferRevisionId;
import com.junbo.common.id.OrganizationId;
import com.junbo.subscription.common.util.Constant;
import org.testng.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        Item item =  new Item();
        item.setOwnerId(new OrganizationId(123L));
        item.setType(ItemType.SUBSCRIPTION.name());

        final String itemId = createItem(item);

        Offer offer = new Offer();
        offer.setOwnerId(new OrganizationId(123L));
        String offerId = createOffer(offer);
        Assert.assertNotNull(offerId);

        OfferRevision offerRevision = new OfferRevision();
        offerRevision.setOfferId(offerId);
        offerRevision.setOwnerId(new OrganizationId(12345L));
        offerRevision.setStatus(Status.DRAFT.name());
        offerRevision.setLocales(new HashMap<String, OfferRevisionLocaleProperties>() {{
            put("en_US", new OfferRevisionLocaleProperties() {{
                setName("subscription-offer");
            }});
        }});

        offerRevision.setItems(new ArrayList<ItemEntry>() {{
            add(new ItemEntry() {{
                setItemId(itemId);
                setQuantity(1);
            }
            });
        }}
        );

        Price price = new Price();
        price.setPriceType(PriceType.FREE.name());
        offerRevision.setPrice(price);

        offerRevision.setEventActions(new HashMap<String, List<Action>>() {{
            put(Constant.EVENT_PURCHASE, new ArrayList<Action>() {{
                add(new Action() {{
                    setType(Constant.ACTION_CHARGE);
                   // setStoredValueAmount(new BigDecimal("123.45"));
                   // setStoredValueCurrency("USD");
                }});
            }});
        }});

        String offerRevisionId = createOfferRevision(offerRevision);
        Assert.assertNotNull(offerRevisionId);

        OfferRevision retrievedRevision = getOfferRevision(offerRevisionId);
        retrievedRevision.setStatus(Status.APPROVED.name());
        updateOfferRevision(retrievedRevision);

        Offer retrieved = catalogGateway.getOffer(offerId, System.currentTimeMillis());
        Assert.assertNotNull(retrieved);


    }

    public String createItem(Item item) {
        try {
            return itemResource.create(item).get().getItemId();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    public String createOffer(Offer offer) {
        try {
            return offerResource.create(offer).get().getOfferId();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    public String createOfferRevision(OfferRevision offerRevision) {
        try {
            return offerRevisionResource.createOfferRevision(offerRevision).get().getRevisionId();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    public OfferRevision getOfferRevision(String offerRevisionId) {
        try {
            return offerRevisionResource.getOfferRevision(offerRevisionId, new OfferRevisionGetOptions()).get();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    public String updateOfferRevision(OfferRevision offerRevision) {
        try {
            return offerRevisionResource.updateOfferRevision(
                    offerRevision.getRevisionId(),
                    offerRevision).get().getRevisionId();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during calling [Catalog] component service.", e);
        }
    }
}
