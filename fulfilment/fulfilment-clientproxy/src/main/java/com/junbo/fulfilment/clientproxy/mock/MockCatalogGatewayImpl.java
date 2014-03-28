/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy.mock;

import com.junbo.catalog.spec.model.offer.*;
import com.junbo.fulfilment.clientproxy.impl.CatalogGatewayImpl;
import com.junbo.fulfilment.common.util.Constant;
import com.junbo.fulfilment.spec.fusion.ShippingMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * MockCatalogGatewayImpl.
 */
public class MockCatalogGatewayImpl extends CatalogGatewayImpl {
    private Map<Long, Offer> mockOffers = new HashMap();

    {
        mockOffers.put(100L, getOffer100());
        mockOffers.put(200L, getOffer200());
        mockOffers.put(300L, getOffer300());
        mockOffers.put(400L, getOffer400());
    }

    @Override
    protected Offer retrieve(Long offerId, Long timestamp) {
        Offer mockOffer = mockOffers.get(offerId);
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
    private Offer getOffer100() {
        Offer offer = new Offer();
        offer.setId(100L);
        offer.setName("offer100");

        offer.setSubOffers(new ArrayList<OfferEntry>() {{
            add(new OfferEntry() {{
                setOfferId(200L);
                setQuantity(2);
            }});

            add(new OfferEntry() {{
                setOfferId(300L);
                setQuantity(3);
            }});
        }});

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

        offer.setEvents(new ArrayList<Event>() {{
            add(new Event() {{
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
    private Offer getOffer200() {
        Offer offer = new Offer();
        offer.setId(200L);
        offer.setName("offer200");

        offer.setItems(new ArrayList<ItemEntry>() {{
            add(new ItemEntry() {{
                setItemId(30000L);
                setQuantity(1);
                setSku(null);
            }});
        }});

        offer.setEvents(new ArrayList<Event>() {{
            add(new Event() {{
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
    private Offer getOffer300() {
        Offer offer = new Offer();
        offer.setId(300L);
        offer.setName("offer300");

        offer.setSubOffers(new ArrayList<OfferEntry>() {{
            add(new OfferEntry() {{
                setOfferId(400L);
                setQuantity(10000);
            }});
        }});

        offer.setItems(new ArrayList<ItemEntry>() {{
            add(new ItemEntry() {{
                setItemId(40000L);
                setQuantity(77);
                setSku(44444L);
            }});
        }});

        offer.setEvents(new ArrayList<Event>() {{
            add(new Event() {{
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
    private Offer getOffer400() {
        Offer offer = new Offer();
        offer.setId(400L);
        offer.setName("offer400");

        offer.setItems(new ArrayList<ItemEntry>() {{
            add(new ItemEntry() {{
                setItemId(50000L);
                setQuantity(9);
                setSku(55555L);
            }});
        }});

        offer.setEvents(new ArrayList<Event>() {{
            add(new Event() {{
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
