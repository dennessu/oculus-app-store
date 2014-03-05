/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.clientproxy.mock;

import com.junbo.catalog.spec.model.promotion.*;
import com.junbo.rating.clientproxy.CatalogGateway;
import com.junbo.rating.spec.fusion.RatingOffer;
import com.junbo.rating.spec.fusion.RatingPrice;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by lizwu on 2/26/14.
 */
public class MockCatalogGatewayImpl implements CatalogGateway {
    @Override
    public RatingOffer getOffer(Long offerId) {
        return generateOffer();
    }

    @Override
    public List<Promotion> getPromotions() {
        List<Promotion> promotions = new ArrayList<Promotion>();
        promotions.add(generatePromotion());
        return promotions;
    }

    private RatingOffer generateOffer() {
        final RatingPrice price = new RatingPrice(new BigDecimal("9.99"), "USD");

        return new RatingOffer() {{
            setId(System.currentTimeMillis());
            setPrices(new HashMap<String, RatingPrice>() {{
                put("US", price);
            }});
            setCategories(new HashSet<Long>());
        }};
    }

    private Promotion generatePromotion() {
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
                /*add(new EntitlementCriterion() {{
                    setPredicate(Predicate.EXCLUDE_ENTITLEMENT);
                    setEntitlements(new ArrayList<Entitlement>() {{
                        add(new Entitlement() {{
                            setId(System.currentTimeMillis());
                            setGroup("XXX");
                            setTag("YYY");
                        }});
                    }});
                }});*/
                add(new ScopeCriterion() {{
                    setPredicate(Predicate.INCLUDE_OFFER);
                    setEntities(new ArrayList<Long>() {{
                        add(100L);
                        add(200L);
                    }});
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
            e.printStackTrace();
        }
        return date;
    }
}
