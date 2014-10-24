/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy.mock;

import com.junbo.catalog.spec.enums.EntitlementType;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.offer.Action;
import com.junbo.catalog.spec.model.offer.ItemEntry;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.fulfilment.clientproxy.impl.CatalogGatewayImpl;
import com.junbo.fulfilment.common.util.Constant;
import com.junbo.fulfilment.spec.fusion.EntitlementMeta;
import com.junbo.fulfilment.spec.fusion.Item;
import com.junbo.fulfilment.spec.fusion.ShippingMethod;

import java.util.*;

/**
 * MockCatalogGatewayImpl.
 */
public class MockCatalogGatewayImpl extends CatalogGatewayImpl {
    private Map<String, OfferRevision> mockOffers = new HashMap();

    {
        mockOffers.put("100L", getOffer100());
        mockOffers.put("200L", getOffer200());
        mockOffers.put("300L", getOffer300());
        mockOffers.put("400L", getOffer400());
    }

    @Override
    protected OfferRevision retrieveOfferRevision(String offerId, Long timestamp) {
        OfferRevision mockOffer = mockOffers.get(offerId);
        if (mockOffer == null) {
            throw new RuntimeException("offer [" + offerId + "] not prepared in mock");
        }

        return mockOffer;
    }

    @Override
    protected ItemRevision retrieveItemRevision(String offerId, Long timestamp) {
        ItemRevision mockItem = new ItemRevision();
        mockItem.setSku("TEST_SKU");

        return mockItem;
    }

    @Override
    protected OfferRevision retrieveLatestOfferRevision(String offerId) {
        OfferRevision mockOffer = mockOffers.get(offerId);
        if (mockOffer == null) {
            throw new RuntimeException("offer [" + offerId + "] not prepared in mock");
        }

        return mockOffer;
    }

    @Override
    protected ItemRevision retrieveLatestItemRevision(String itemId) {
        ItemRevision mockItem = new ItemRevision();
        mockItem.setSku("TEST_SKU");

        return mockItem;
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
        offer.setOfferId("100L");

        offer.setSubOffers(Arrays.asList("200L", "300L"));

        offer.setItems(new ArrayList<ItemEntry>() {{
            add(new ItemEntry() {{
                setItemId("10000L");
                setQuantity(888);
            }});
            add(new ItemEntry() {{
                setItemId("20000L");
                setQuantity(999);
            }});
        }});

        offer.setEventActions(new HashMap<String, List<Action>>() {{
            put(Constant.EVENT_PURCHASE, new ArrayList<Action>() {{
                add(new Action() {{
                    setType(Constant.ACTION_DELIVER_PHYSICAL_GOODS);
                }});
                add(new Action() {{
                    setType(Constant.ACTION_GRANT_ENTITLEMENT);
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
        offer.setOfferId("200L");

        offer.setItems(new ArrayList<ItemEntry>() {{
            add(new ItemEntry() {{
                setItemId("30000L");
                setQuantity(1);
            }});
        }});

        offer.setEventActions(new HashMap<String, List<Action>>() {{
            put(Constant.EVENT_PURCHASE, new ArrayList<Action>() {{
                add(new Action() {{
                    setType(Constant.ACTION_GRANT_ENTITLEMENT);
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
        offer.setOfferId("300L");

        offer.setSubOffers(Arrays.asList("400L"));

        offer.setItems(new ArrayList<ItemEntry>() {{
            add(new ItemEntry() {{
                setItemId("40000L");
                setQuantity(77);
            }});
        }});

        offer.setEventActions(new HashMap<String, List<Action>>() {{
            put(Constant.EVENT_PURCHASE, new ArrayList<Action>() {{
                add(new Action() {{
                    setType(Constant.ACTION_DELIVER_PHYSICAL_GOODS);
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
        offer.setOfferId("400L");

        offer.setItems(new ArrayList<ItemEntry>() {{
            add(new ItemEntry() {{
                setItemId("50000L");
                setQuantity(9);
            }});
        }});

        offer.setEventActions(new HashMap<String, List<Action>>() {{
            put(Constant.EVENT_PURCHASE, new ArrayList<Action>() {{
                add(new Action() {{
                    setType(Constant.ACTION_DELIVER_PHYSICAL_GOODS);
                }});
            }});
        }});

        return offer;
    }

    @Override
    public ShippingMethod getShippingMethod(String shippingMethodId) {
        ShippingMethod result = new ShippingMethod();
        result.setId(shippingMethodId);

        return result;
    }

    @Override
    public Item getItem(String itemId, Long timestamp) {
        Item item = new Item();
        item.setItemId("123L");
        item.setSku("TEST_SKU");
        item.setEntitlementMetas(new ArrayList<EntitlementMeta>() {{
            add(new EntitlementMeta() {{
                setType(EntitlementType.DOWNLOAD.toString());
                setConsumable(false);
            }});
        }});

        return item;
    }
}
