/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.clientproxy.mock;

import com.junbo.catalog.spec.model.attribute.Attribute;
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
 * Created by lizwu on 2/26/14.
 * @deprecated just for internal test
 */

@Deprecated
public class MockCatalogGatewayImpl implements CatalogGateway {
    Map<Long, Attribute> mockAttributes = new HashMap<Long, Attribute>() {{
        put(400L, new Attribute() {{
            setId(400L);
            setName("DIGITAL");
        }});
        put(401L, new Attribute() {{
            setId(401L);
            setName("PHYSICAL");
        }});
    }};

    Map<Long, RatingOffer> mockOffers = new HashMap<Long, RatingOffer>() {{
        put(100L, genOffer100());
        put(102L, genOffer102());
        put(109L, genOffer109());
    }};

    Map<Long, Item> mockItems = new HashMap<Long, Item>() {{
        put(200L, genDigitalItem());
        put(201L, genPhysicalItem());
    }};

    Map<Long, Promotion> mockPromotions = new HashMap<Long, Promotion>() {{
        put(300L, genOfferPro());
        put(301L, genOfferProWithExEntitlement());
        put(302L, genOrderPromotion());

    }};


    @Override
    public Attribute getAttribute(Long attributeId) {
        return mockAttributes.get(attributeId);
    }

    @Override
    public Item getItem(Long itemId) {
        return mockItems.get(itemId);
    }

    @Override
    public RatingOffer getOffer(Long offerId) {
        return mockOffers.get(offerId);
    }

    @Override
    public List<Promotion> getPromotions() {
        return new ArrayList<Promotion>(mockPromotions.values());
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
        final Price price = new Price(new BigDecimal("9.99"), "USD");

        return new RatingOffer() {{
            setId(100L);
            setPrices(new HashMap<String, Price>() {{
                put("US", price);
            }});
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
        final Price price = new Price(new BigDecimal("9.99"), "USD");

        return new RatingOffer() {{
            setId(102L);
            setPrices(new HashMap<String, Price>() {{
                put("US", price);
            }});
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
                    setQuantity(2);
                }});
            }});
        }};
    }

    private RatingOffer genOffer109() {
        final Price price = new Price(new BigDecimal("9.99"), "USD");

        return new RatingOffer() {{
            setId(109L);
            setPrices(new HashMap<String, Price>() {{
                put("US", price);
            }});
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
            setId(200L);
            setType(400L);
        }};
    }

    private Item genPhysicalItem() {
        return new Item() {{
            setId(201L);
            setType(401L);
        }};
    }

    private Promotion genOfferPro() {
        final Benefit benefit = new Benefit();
        benefit.setType(BenefitType.FIXED_PRICE);
        benefit.setValue(new BigDecimal("1.99"));

        return new Promotion() {{
            setId(System.currentTimeMillis());
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

    private Promotion genOfferProWithExEntitlement() {
        final Benefit benefit = new Benefit();
        benefit.setType(BenefitType.FIXED_PRICE);
        benefit.setValue(new BigDecimal("0.99"));

        return new Promotion() {{
            setId(System.currentTimeMillis());
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
                            setId(System.currentTimeMillis());
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

    private Promotion genOrderPromotion() {
        final Benefit benefit = new Benefit();
        benefit.setType(BenefitType.FLAT_DISCOUNT);
        benefit.setValue(new BigDecimal("5.00"));

        return new Promotion() {{
            setId(System.currentTimeMillis());
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
                            setId(System.currentTimeMillis());
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
