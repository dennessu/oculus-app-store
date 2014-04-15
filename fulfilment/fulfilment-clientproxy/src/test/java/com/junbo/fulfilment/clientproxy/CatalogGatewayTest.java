/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy;

import com.junbo.catalog.spec.model.common.LocalizableProperty;
import com.junbo.catalog.spec.model.common.Price;
import com.junbo.catalog.spec.model.common.Status;
import com.junbo.catalog.spec.model.offer.Action;
import com.junbo.catalog.spec.model.offer.Event;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.fulfilment.common.util.Constant;
import junit.framework.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * EntitlementGatewayTest.
 */
public class CatalogGatewayTest extends BaseTest {
    @Autowired
    private CatalogGateway gateway;

    @Test(enabled = true)
    public void testBVT() {
        Offer offer = new Offer();
        offer.setOwnerId(123L);

        LocalizableProperty name = new LocalizableProperty();
        name.set("en_US", "test_offer_name");
        offer.setName(name);
        Long offerId = megaGateway.createOffer(offer);
        Assert.assertNotNull(offerId);

        OfferRevision offerRevision = new OfferRevision();
        offerRevision.setOfferId(offerId);
        offerRevision.setOwnerId(12345L);
        offerRevision.setStatus(Status.DRAFT);

        Price price = new Price();
        price.setPriceType(Price.FREE);
        offerRevision.setPrice(price);
        offerRevision.setEvents(new HashMap<String, Event>() {{
            put(Constant.EVENT_PURCHASE.toLowerCase(), new Event() {{
                setName(Constant.EVENT_PURCHASE);
                setActions(new ArrayList<Action>() {{
                    add(new Action() {{
                        setType(Constant.ACTION_GRANT_ENTITLEMENT);
                        setProperties(new HashMap<String, Object>() {{
                            put(Constant.ENTITLEMENT_DEF_ID, "12345");
                        }});
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
}
