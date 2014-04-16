/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.mock;

import com.junbo.catalog.spec.model.domaindata.ShippingMethod;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.promotion.*;
import com.junbo.rating.clientproxy.CatalogGateway;
import com.junbo.rating.spec.fusion.EntryType;
import com.junbo.rating.spec.fusion.LinkedEntry;
import com.junbo.rating.spec.fusion.Price;
import com.junbo.rating.spec.fusion.RatingOffer;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by lizwu on 3/28/14.
 */
public class MockCatalogGatewayImpl implements CatalogGateway{
    Map<Long, RatingOffer> mockOffers = new HashMap<Long, RatingOffer>() {{
        put(100L, genOffer100());
        put(102L, genOffer102());
        put(109L, genOffer109());
    }};

    Map<Long, Item> mockItems = new HashMap<Long, Item>() {{
        put(200L, genDigitalItem());
        put(201L, genPhysicalItem());
    }};

    Map<Long, PromotionRevision> mockPromotions = new HashMap<Long, PromotionRevision>() {{
        put(300L, genOfferPro());
        put(301L, genOfferProWithExEntitlement());
        put(302L, genOrderPromotion());

    }};

    @Override
    public Item getItem(Long itemId) {
        return mockItems.get(itemId);
    }

    @Override
    public RatingOffer getOffer(Long offerId, Long timestamp) {
        return mockOffers.get(offerId);
    }

    @Override
    public List<PromotionRevision> getPromotions() {
        return new ArrayList<>(mockPromotions.values());
    }

    @Override
    public ShippingMethod getShippingMethod(Long shippingMethodId) {
        return new ShippingMethod() {{
            setId(System.currentTimeMillis());
            setBaseUnit(10);
            setCapUnit(20);
            setBasePrice(new BigDecimal("10.00"));
            setAdditionalPrice(new BigDecimal("1.20"));
        }};
    }


    private RatingOffer genOffer100() {
        return new RatingOffer() {{
            setId(100L);
            setPrice(new Price(Price.CUSTOM, new HashMap<String, BigDecimal>() {{
                put("USD", new BigDecimal("9.99"));
            }}));
            setCategories(new ArrayList<Long>());
            setItems(new ArrayList<LinkedEntry>() {{
                add(new LinkedEntry() {{
                    setEntryId(200L);
                    setType(EntryType.ITEM);
                    setQuantity(1);
                }});
                add(new LinkedEntry() {{
                    setEntryId(201L);
                    setType(EntryType.ITEM);
                    setQuantity(1);
                }});
            }});
        }};
    }

    private RatingOffer genOffer102() {
        return new RatingOffer() {{
            setId(102L);
            setPrice(new Price(Price.CUSTOM, new HashMap<String, BigDecimal>() {{
                put("USD", new BigDecimal("9.99"));
            }}));
            setCategories(new ArrayList<Long>());
            setItems(new ArrayList<LinkedEntry>() {{
                add(new LinkedEntry() {{
                    setEntryId(201L);
                    setType(EntryType.ITEM);
                    setQuantity(2);
                }});
            }});
            setSubOffers(new ArrayList<LinkedEntry>() {{
                add(new LinkedEntry() {{
                    setEntryId(100L);
                    setType(EntryType.OFFER);
                }});
            }});
        }};
    }

    private RatingOffer genOffer109() {
        return new RatingOffer() {{
            setId(109L);
            setPrice(new Price(Price.CUSTOM, new HashMap<String, BigDecimal>() {{
                put("USD", new BigDecimal("9.99"));
            }}));
            setCategories(new ArrayList<Long>());
            setSubOffers(new ArrayList<LinkedEntry>() {{
                add(new LinkedEntry() {{
                    setEntryId(100L);
                    setType(EntryType.OFFER);
                    setQuantity(1);
                }});
                add(new LinkedEntry() {{
                    setEntryId(102L);
                    setType(EntryType.OFFER);
                    setQuantity(1);
                }});
            }});
        }};
    }

    private Item genDigitalItem() {
        return new Item() {{
            setItemId(200L);
            setType("DIGITAL");
        }};
    }

    private Item genPhysicalItem() {
        return new Item() {{
            setItemId(201L);
            setType("PHYSICAL");
        }};
    }

    private PromotionRevision genOfferPro() {
        final Benefit benefit = new Benefit();
        benefit.setType(BenefitType.FIXED_PRICE);
        benefit.setValue(new BigDecimal("1.99"));

        return new PromotionRevision() {{
            setRevisionId(System.currentTimeMillis());
            setType(PromotionType.OFFER_PROMOTION);
            setCurrency("USD");
            setStartDate(generateDate("2014-01-01 00:00:00"));
            setEndDate(generateDate("2014-12-29 00:00:00"));
            setBenefit(benefit);
            setCriteria(new ArrayList<Criterion>() {{
                add(new ScopeCriterion() {{
                    setPredicate(Predicate.INCLUDE_OFFER);
                    setEntities(new ArrayList<Long>() {{
                        add(102L);
                        add(103L);
                    }});
                }});
            }});
        }};
    }

    private PromotionRevision genOfferProWithExEntitlement() {
        final Benefit benefit = new Benefit();
        benefit.setType(BenefitType.FIXED_PRICE);
        benefit.setValue(new BigDecimal("0.99"));

        return new PromotionRevision() {{
            setRevisionId(System.currentTimeMillis());
            setType(PromotionType.OFFER_PROMOTION);
            setCurrency("USD");
            setStartDate(generateDate("2014-01-01 00:00:00"));
            setEndDate(generateDate("2014-12-29 00:00:00"));
            setBenefit(benefit);
            setCriteria(new ArrayList<Criterion>() {{
                add(new EntitlementCriterion() {{
                    setPredicate(Predicate.EXCLUDE_ENTITLEMENT);
                    setEntitlements(new ArrayList<Entitlement>() {{
                        add(new Entitlement() {{
                            setGroup("XXX");
                            setTag("YYY");
                        }});
                    }});
                }});
                add(new ScopeCriterion() {{
                    setPredicate(Predicate.INCLUDE_OFFER);
                    setEntities(new ArrayList<Long>() {{
                        add(100L);
                        add(101L);
                    }});
                }});
            }});
        }};
    }

    private PromotionRevision genOrderPromotion() {
        final Benefit benefit = new Benefit();
        benefit.setType(BenefitType.FLAT_DISCOUNT);
        benefit.setValue(new BigDecimal("5.00"));

        return new PromotionRevision() {{
            setRevisionId(System.currentTimeMillis());
            setType(PromotionType.ORDER_PROMOTION);
            setCurrency("USD");
            setStartDate(generateDate("2014-01-01 00:00:00"));
            setEndDate(generateDate("2014-12-29 00:00:00"));
            setBenefit(benefit);
            setCriteria(new ArrayList<Criterion>() {{
                add(new EntitlementCriterion() {{
                    setPredicate(Predicate.INCLUDE_ENTITLEMENT);
                    setEntitlements(new ArrayList<Entitlement>() {{
                        add(new Entitlement() {{
                            setGroup("XXX");
                            setTag("YYY");
                        }});
                    }});
                }});
                add(new OrderCriterion() {{
                    setPredicate(Predicate.ORDER_ABSOLUTE_VALUE_ABOVE);
                    setThresholdValue(new BigDecimal("20.00"));
                }});
            }});
        }};
    }

    private Date generateDate(String myDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = formatter.parse(myDate);
        } catch (ParseException e) {
        }
        return date;
    }
}
