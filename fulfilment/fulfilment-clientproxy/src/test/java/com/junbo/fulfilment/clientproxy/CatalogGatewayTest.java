/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy;

import com.junbo.catalog.spec.model.offer.Action;
import com.junbo.catalog.spec.model.offer.Event;
import com.junbo.catalog.spec.model.offer.Offer;
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

    @Test(enabled = false)
    public void testBVT() {
        Offer offer = new Offer();
        offer.setName("TEST_OFFER");
        offer.setOwnerId(12345L);

        offer.setEvents(new ArrayList<Event>() {{
            add(new Event() {{
                setName(Constant.EVENT_PURCHASE);
                setActions(new ArrayList<Action>() {{
                    add(new Action() {{
                        setType(Constant.ACTION_GRANT_ENTITLEMENT);
                        setProperties(new HashMap<String, String>() {{
                            put(Constant.ENTITLEMENT_DEF_ID, "12345");
                        }});
                    }});
                }});
            }});
        }});

        Long offerId = megaGateway.createOffer(offer);
        Assert.assertNotNull(offerId);

        offer.setId(offerId);
        offer.setStatus("RELEASED");
        megaGateway.updateOffer(offer);

        com.junbo.fulfilment.spec.fusion.Offer retrieved = gateway.getOffer(offerId);
        Assert.assertNotNull(retrieved);
    }
}
