/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy;

import com.junbo.catalog.spec.model.common.LocalizableProperty;
import com.junbo.catalog.spec.model.common.Price;
import com.junbo.catalog.spec.model.common.Status;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.item.ItemRevisionLocaleProperties;
import com.junbo.catalog.spec.model.item.ItemType;
import com.junbo.catalog.spec.model.offer.*;
import com.junbo.fulfilment.common.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * EntitlementGatewayTest.
 */
public class CatalogGatewayTest extends BaseTest {
    @Autowired
    private CatalogGateway gateway;

    @Test(enabled = false)
    public void testBVT() {
        Offer offer = new Offer();
        offer.setOwnerId(123L);

        LocalizableProperty name = new LocalizableProperty();
        name.set("DEFAULT", "test_offer_name");
        Long offerId = megaGateway.createOffer(offer);
        Assert.assertNotNull(offerId);

        OfferRevision offerRevision = new OfferRevision();
        offerRevision.setOfferId(offerId);
        offerRevision.setOwnerId(12345L);
        offerRevision.setStatus(Status.DRAFT);
        offerRevision.setLocales(new HashMap<String, OfferRevisionLocaleProperties>() {{
            put("en_US", new OfferRevisionLocaleProperties() {{
                setName("test-offer");
            }});
        }});

        Price price = new Price();
        price.setPriceType(Price.FREE);
        offerRevision.setPrice(price);
        offerRevision.setEventActions(new HashMap<String, List<Action>>() {{
            put(Constant.EVENT_PURCHASE,
                    new ArrayList<Action>() {{
                        add(new Action() {{
                            setType(Constant.ACTION_GRANT_ENTITLEMENT);
                            setProperties(new HashMap<String, Object>() {{
                                put(Constant.ENTITLEMENT_DEF_ID, "12345");
                            }});
                        }});
                    }});
        }});

        Long offerRevisionId = megaGateway.createOfferRevision(offerRevision);
        Assert.assertNotNull(offerRevisionId);

        OfferRevision retrievedRevision = megaGateway.getOfferRevision(offerRevisionId);
        retrievedRevision.setStatus(Status.APPROVED);
        megaGateway.updateOfferRevision(retrievedRevision);

        com.junbo.fulfilment.spec.fusion.Offer retrieved = gateway.getOffer(offerId, System.currentTimeMillis());
        Assert.assertNotNull(retrieved);
    }

    @Test(enabled = false)
    public void testWalletBVT() {
        LocalizableProperty name = new LocalizableProperty();
        name.set("DEFAULT", "test_offer_name");
        Long ownerId = 123L;

        // create item
        Item item = new Item();
        item.setType(ItemType.WALLET);
        item.setOwnerId(ownerId);
        item.setSku("test_sku");

        final Long itemId = megaGateway.createItem(item);
        Assert.assertNotNull(itemId);

        // create item revision
        ItemRevision itemRevision = new ItemRevision();
        itemRevision.setItemId(itemId);
        itemRevision.setOwnerId(ownerId);
        itemRevision.setType(ItemType.WALLET);
        itemRevision.setWalletAmount(new BigDecimal(123.45));
        itemRevision.setWalletCurrency("USD");
        itemRevision.setWalletCurrencyType("REAL_CURRENCY");
        itemRevision.setStatus(Status.DRAFT);
        itemRevision.setLocales(new HashMap<String, ItemRevisionLocaleProperties>() {{
            put("en_US", new ItemRevisionLocaleProperties() {{
                setName("test-offer");
            }});
        }});

        Long itemRevisionId = megaGateway.createItemRevision(itemRevision);
        Assert.assertNotNull(itemRevisionId);

        // approve item
        ItemRevision retrievedItemRevision = megaGateway.getItemRevision(itemRevisionId);
        retrievedItemRevision.setStatus(Status.APPROVED);
        megaGateway.updateItemRevision(retrievedItemRevision);

        // create offer
        Offer offer = new Offer();
        offer.setOwnerId(ownerId);

        Long offerId = megaGateway.createOffer(offer);
        Assert.assertNotNull(offerId);

        // create offer revision
        OfferRevision offerRevision = new OfferRevision();
        offerRevision.setOfferId(offerId);
        offerRevision.setOwnerId(ownerId);
        offerRevision.setStatus(Status.DRAFT);

        Price price = new Price();
        price.setPriceType(Price.FREE);
        offerRevision.setPrice(price);
        offerRevision.setEventActions(new HashMap<String, List<Action>>() {{
            put(Constant.EVENT_PURCHASE.toLowerCase(), new ArrayList<Action>() {{
                add(new Action() {{
                    setType(Constant.ACTION_CREDIT_WALLET);
                }});
            }});
        }});

        offerRevision.setItems(new ArrayList<ItemEntry>() {{
            add(new ItemEntry() {{
                setItemId(itemId);
                setQuantity(1);
            }});
        }});

        Long offerRevisionId = megaGateway.createOfferRevision(offerRevision);
        Assert.assertNotNull(offerRevisionId);

        OfferRevision retrievedOfferRevision = megaGateway.getOfferRevision(offerRevisionId);
        retrievedOfferRevision.setStatus(Status.APPROVED);
        megaGateway.updateOfferRevision(retrievedOfferRevision);

        com.junbo.fulfilment.spec.fusion.Offer retrieved = gateway.getOffer(offerId, System.currentTimeMillis());
        Assert.assertNotNull(retrieved);
    }
}
