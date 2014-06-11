/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.mock;

import com.junbo.catalog.spec.enums.PriceType;
import com.junbo.catalog.spec.model.domaindata.ShippingMethod;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.promotion.*;
import com.junbo.catalog.spec.model.promotion.criterion.*;
import com.junbo.rating.clientproxy.CatalogGateway;
import com.junbo.rating.common.util.Constants;
import com.junbo.rating.common.util.Utils;
import com.junbo.rating.spec.fusion.*;
import com.junbo.rating.spec.fusion.Properties;

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
        put(107L, genOffer107());
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
        put(303L, genEffectOfferProWithInExEntitlement());

    }};

    @Override
    public Item getItem(Long itemId) {
        return mockItems.get(itemId);
    }

    @Override
    public RatingOffer getOffer(Long offerId, String timestamp) {
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
            setBaseUnit(5);
            setCapUnit(10);
            setBasePrice(new BigDecimal("10.00"));
            setAdditionalPrice(new BigDecimal("1.20"));
        }};
    }

    private RatingOffer genOffer100() {
        return new RatingOffer() {{
            setId(100L);
            setPrice(new Price(PriceType.CUSTOM.name(), new HashMap<String, Map<String, BigDecimal>>() {{
                put("US", new HashMap<String, BigDecimal>() {{
                    put("USD", new BigDecimal("9.99"));
                }});
            }}));
            setPreOrderPrice(new Price(PriceType.CUSTOM.name(), new HashMap<String, Map<String, BigDecimal>>() {{
                put("US", new HashMap<String, BigDecimal>() {{
                    put("USD", new BigDecimal("0.99"));
                }});
            }}));
            setCountries(new HashMap<String, Properties>() {{
                put("US", new Properties(true, Utils.maxDate()));
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
            setEventActions(new HashMap<String, List<OfferAction>>() {{
                put(Constants.PURCHASE_EVENT, new ArrayList<OfferAction>() {{
                    add(new OfferAction() {{
                        setType(Constants.CHARGE_ACTION);
                        setPrice(new Price(PriceType.CUSTOM.name(), new HashMap<String, Map<String, BigDecimal>>() {{
                            put("US", new HashMap<String, BigDecimal>() {{
                                put("USD", new BigDecimal("9.99"));
                            }});
                        }}));
                    }});
                }});
            }});
        }};
    }

    private RatingOffer genOffer102() {
        return new RatingOffer() {{
            setId(102L);
            setPrice(new Price(PriceType.CUSTOM.name(), new HashMap<String, Map<String, BigDecimal>>() {{
                put("US", new HashMap<String, BigDecimal>() {{
                    put("USD", new BigDecimal("9.99"));
                }});
            }}));
            setCountries(new HashMap<String, Properties>() {{
                put("US", new Properties(true, Utils.maxDate()));
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
                }});
            }});
        }};
    }

    private RatingOffer genOffer107() {
        return new RatingOffer() {{
            setId(107L);
            setPrice(new Price(PriceType.CUSTOM.name(), new HashMap<String, Map<String, BigDecimal>>() {{
                put("US", new HashMap<String, BigDecimal>() {{
                    put("USD", new BigDecimal("1.99"));
                }});
            }}));
            setCountries(new HashMap<String, Properties>() {{
                put("US", new Properties(true, Utils.maxDate()));
            }});
            setCategories(new ArrayList<Long>());
            setItems(new ArrayList<LinkedEntry>() {{
                add(new LinkedEntry() {{
                    setEntryId(200L);
                    setType(EntryType.ITEM);
                    setQuantity(1);
                }});
            }});
        }};
    }

    private RatingOffer genOffer109() {
        return new RatingOffer() {{
            setId(109L);
            setPrice(new Price(PriceType.CUSTOM.name(), new HashMap<String, Map<String, BigDecimal>>() {{
                put("US", new HashMap<String, BigDecimal>() {{
                    put("USD", new BigDecimal("9.99"));
                }});
            }}));
            setCountries(new HashMap<String, Properties>() {{
                put("US", new Properties(true, Utils.maxDate()));
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
                add(new OfferScopeCriterion() {{
                    setPredicate(Predicate.INCLUDE_OFFER);
                    setOffers(new ArrayList<Long>() {{
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
                    setItems(new ArrayList<Long>() {{
                        add(400L);
                    }});
                }});
                add(new OfferScopeCriterion() {{
                    setPredicate(Predicate.INCLUDE_OFFER);
                    setOffers(new ArrayList<Long>() {{
                        add(100L);
                        add(101L);
                    }});
                }});
            }});
        }};
    }

    private PromotionRevision genEffectOfferProWithInExEntitlement() {
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
                    setItems(new ArrayList<Long>() {{
                        add(401L);
                        add(402L);
                    }});
                }});
                add(new EntitlementCriterion() {{
                    setPredicate(Predicate.INCLUDE_ENTITLEMENT);
                    setItems(new ArrayList<Long>() {{
                        add(400L);
                        add(403L);
                    }});
                }});
                add(new OfferScopeCriterion() {{
                    setPredicate(Predicate.INCLUDE_OFFER);
                    setOffers(new ArrayList<Long>() {{
                        add(107L);
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
                    setItems(new ArrayList<Long>() {{
                        add(400L);
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
