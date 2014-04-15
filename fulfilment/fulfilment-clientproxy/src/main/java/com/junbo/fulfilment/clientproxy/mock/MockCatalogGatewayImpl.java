/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy.mock;

import com.junbo.catalog.spec.model.offer.Action;
import com.junbo.catalog.spec.model.offer.Event;
import com.junbo.catalog.spec.model.offer.ItemEntry;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.fulfilment.clientproxy.impl.CatalogGatewayImpl;
import com.junbo.fulfilment.common.util.Constant;
import com.junbo.fulfilment.spec.fusion.ShippingMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * MockCatalogGatewayImpl.
 */
public class MockCatalogGatewayImpl extends CatalogGatewayImpl {
    private Map<Long, OfferRevision> mockOffers = new HashMap();

    {
        mockOffers.put(100L, getOffer100());
        mockOffers.put(200L, getOffer200());
        mockOffers.put(300L, getOffer300());
        mockOffers.put(400L, getOffer400());
    }

    @Override
    protected OfferRevision retrieveOfferRevision(Long offerId, Long timestamp) {
        OfferRevision mockOffer = mockOffers.get(offerId);
        if (mockOffer == null) {
            throw new RuntimeException("offer [" + offerId + "] not prepared in mock");
        }

        return mockOffer;
    }

    /**
     * offer 100 is a bundle offers.
     * - offer200 *2
     * - offer300 *3
     * - item10000 *888
     * - item20000 *999
     * <p/>
     * fulfilment actions contains:
     * - GRANT_ENTITLEMENT
     * - DELIVER_PHYSICAL_GOODS
     */
    private OfferRevision getOffer100() {
        OfferRevision offer = new OfferRevision();
        offer.setOfferId(100L);

        offer.setSubOffers(Arrays.asList(200L, 300L));

        offer.setItems(new ArrayList<ItemEntry>() {{
            add(new ItemEntry() {{
                setItemId(10000L);
                setQuantity(888);
                setSku(11111L);
            }});
            add(new ItemEntry() {{
                setItemId(20000L);
                setQuantity(999);
                setSku(22222L);
            }});
        }});

        offer.setEvents(new HashMap<String, Event>() {{
            put(Constant.EVENT_PURCHASE.toUpperCase(), new Event() {{
                setName(Constant.EVENT_PURCHASE);
                setActions(new ArrayList<Action>() {{
                    add(new Action() {{
                        setType(Constant.ACTION_DELIVER_PHYSICAL_GOODS);
                    }});
                    add(new Action() {{
                        setType(Constant.ACTION_GRANT_ENTITLEMENT);
                        setProperties(new HashMap<String, Object>() {{
                            put(Constant.ENTITLEMENT_DEF_ID, "12345");
                        }});
                    }});
                }});
            }});
        }});

        return offer;
    }

    /**
     * offer 200 is a pure DIGITAL offers.
     * - item30000 *1
     * <p/>
     * fulfilment actions contains:
     * - GRANT_ENTITLEMENT
     */
    private OfferRevision getOffer200() {
        OfferRevision offer = new OfferRevision();
        offer.setOfferId(200L);

        offer.setItems(new ArrayList<ItemEntry>() {{
            add(new ItemEntry() {{
                setItemId(30000L);
                setQuantity(1);
                setSku(null);
            }});
        }});

        offer.setEvents(new HashMap<String, Event>() {{
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

        return offer;
    }

    /**
     * offer 300 is a bundle PHYSICAL offers.
     * - offer400 * 10000
     * - item40000 *77
     * <p/>
     * fulfilment actions contains:
     * - DELIVER_PHYSICAL_GOODS
     */
    private OfferRevision getOffer300() {
        OfferRevision offer = new OfferRevision();
        offer.setOfferId(300L);

        offer.setSubOffers(Arrays.asList(400L));

        offer.setItems(new ArrayList<ItemEntry>() {{
            add(new ItemEntry() {{
                setItemId(40000L);
                setQuantity(77);
                setSku(44444L);
            }});
        }});

        offer.setEvents(new HashMap<String, Event>() {{
            put(Constant.EVENT_PURCHASE.toLowerCase(), new Event() {{
                setName(Constant.EVENT_PURCHASE);
                setActions(new ArrayList<Action>() {{
                    add(new Action() {{
                        setType(Constant.ACTION_DELIVER_PHYSICAL_GOODS);
                    }});
                }});
            }});
        }});

        return offer;
    }

    /**
     * offer 400 is a pure PHYSICAL offers.
     * - item50000 * 9
     * <p/>
     * fulfilment actions contains:
     * - DELIVER_PHYSICAL_GOODS
     */
    private OfferRevision getOffer400() {
        OfferRevision offer = new OfferRevision();
        offer.setOfferId(400L);

        offer.setItems(new ArrayList<ItemEntry>() {{
            add(new ItemEntry() {{
                setItemId(50000L);
                setQuantity(9);
                setSku(55555L);
            }});
        }});

        offer.setEvents(new HashMap<String, Event>() {{
            put(Constant.EVENT_PURCHASE.toLowerCase(), new Event() {{
                setName(Constant.EVENT_PURCHASE);
                setActions(new ArrayList<Action>() {{
                    add(new Action() {{
                        setType(Constant.ACTION_DELIVER_PHYSICAL_GOODS);
                    }});
                }});
            }});
        }});

        return offer;
    }

    @Override
    public ShippingMethod getShippingMethod(Long shippingMethodId) {
        ShippingMethod result = new ShippingMethod();
        result.setId(shippingMethodId);

        return result;
    }

    @Override
    protected Map<String, Object> getEntitlementDef(Action action) {
        Map<String, Object> result = new HashMap<>();

        result.put(Constant.ENTITLEMENT_GROUP, "TEST_GROUP");
        result.put(Constant.ENTITLEMENT_TAG, "TEST_TAG");
        result.put(Constant.ENTITLEMENT_TYPE, "DOWNLOAD");
        result.put(Constant.ENTITLEMENT_DEVELOPER, "TEST_DEVELOPER");

        return result;
    }
}
