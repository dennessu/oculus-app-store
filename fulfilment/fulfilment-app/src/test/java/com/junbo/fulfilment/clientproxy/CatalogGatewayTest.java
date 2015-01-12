/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy;

import com.junbo.catalog.spec.enums.ItemType;
import com.junbo.catalog.spec.enums.PriceType;
import com.junbo.catalog.spec.enums.Status;
import com.junbo.catalog.spec.model.common.Price;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.item.ItemRevisionLocaleProperties;
import com.junbo.catalog.spec.model.offer.*;
import com.junbo.common.id.OrganizationId;
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
        offer.setOwnerId(new OrganizationId(123L));

        String offerId = megaGateway.createOffer(offer);
        Assert.assertNotNull(offerId);

        OfferRevision offerRevision = new OfferRevision();
        offerRevision.setOfferId(offerId);
        offerRevision.setOwnerId(new OrganizationId(12345L));
        offerRevision.setStatus(Status.DRAFT.name());
        offerRevision.setLocales(new HashMap<String, OfferRevisionLocaleProperties>() {{
            put("en_US", new OfferRevisionLocaleProperties() {{
                setName("test-offer");
            }});
        }});

        Price price = new Price();
        price.setPriceType(PriceType.FREE.name());
        offerRevision.setPrice(price);
        offerRevision.setEventActions(new HashMap<String, List<Action>>() {{
            put(Constant.EVENT_PURCHASE,
                    new ArrayList<Action>() {{
                        add(new Action() {{
                            setType(Constant.ACTION_GRANT_ENTITLEMENT);
                        }});
                    }});
        }});

        String offerRevisionId = megaGateway.createOfferRevision(offerRevision);
        Assert.assertNotNull(offerRevisionId);

        OfferRevision retrievedRevision = megaGateway.getOfferRevision(offerRevisionId);
        retrievedRevision.setStatus(Status.APPROVED.name());
        megaGateway.updateOfferRevision(retrievedRevision);

        com.junbo.fulfilment.spec.fusion.Offer retrieved = gateway.getOffer(offerId, System.currentTimeMillis());
        Assert.assertNotNull(retrieved);
    }

    @Test(enabled = false)
    public void testWalletBVT() {
        OrganizationId ownerId = new OrganizationId(123L);

        // create item
        Item item = new Item();
        item.setType(ItemType.EWALLET.name());
        item.setOwnerId(ownerId);

        final String itemId = megaGateway.createItem(item);
        Assert.assertNotNull(itemId);

        // create item revision
        ItemRevision itemRevision = new ItemRevision();
        itemRevision.setItemId(itemId);
        itemRevision.setOwnerId(ownerId);
        itemRevision.setStatus(Status.DRAFT.name());
        itemRevision.setLocales(new HashMap<String, ItemRevisionLocaleProperties>() {{
            put("en_US", new ItemRevisionLocaleProperties() {{
                setName("test-offer");
            }});
        }});
        itemRevision.setSku("test_sku");

        String itemRevisionId = megaGateway.createItemRevision(itemRevision);
        Assert.assertNotNull(itemRevisionId);

        // approve item
        ItemRevision retrievedItemRevision = megaGateway.getItemRevision(itemRevisionId);
        retrievedItemRevision.setStatus(Status.APPROVED.name());
        megaGateway.updateItemRevision(retrievedItemRevision);

        // create offer
        Offer offer = new Offer();
        offer.setOwnerId(ownerId);

        String offerId = megaGateway.createOffer(offer);
        Assert.assertNotNull(offerId);

        // create offer revision
        OfferRevision offerRevision = new OfferRevision();
        offerRevision.setOfferId(offerId);
        offerRevision.setOwnerId(ownerId);
        offerRevision.setStatus(Status.DRAFT.name());
        offerRevision.setLocales(new HashMap<String, OfferRevisionLocaleProperties>() {{
            put("en_US", new OfferRevisionLocaleProperties() {{
                setName("test-offer");
            }});
        }});

        Price price = new Price();
        price.setPriceType(PriceType.FREE.name());
        offerRevision.setPrice(price);
        offerRevision.setEventActions(new HashMap<String, List<Action>>() {{
            put(Constant.EVENT_PURCHASE, new ArrayList<Action>() {{
                add(new Action() {{
                    setType(Constant.ACTION_CREDIT_WALLET);
                    setStoredValueAmount(new BigDecimal("123.45"));
                    setStoredValueCurrency("USD");
                }});
            }});
        }});

        offerRevision.setItems(new ArrayList<ItemEntry>() {{
            add(new ItemEntry() {{
                setItemId(itemId);
                setQuantity(1);
            }});
        }});

        String offerRevisionId = megaGateway.createOfferRevision(offerRevision);
        Assert.assertNotNull(offerRevisionId);

        OfferRevision retrievedOfferRevision = megaGateway.getOfferRevision(offerRevisionId);
        retrievedOfferRevision.setStatus(Status.APPROVED.name());
        megaGateway.updateOfferRevision(retrievedOfferRevision);

        com.junbo.fulfilment.spec.fusion.Offer retrieved = gateway.getOffer(offerId, System.currentTimeMillis());
        Assert.assertNotNull(retrieved);
    }
}
