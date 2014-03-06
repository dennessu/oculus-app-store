/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.BaseTest;
import com.junbo.catalog.db.entity.PromotionDraftEntity;
import com.junbo.catalog.spec.model.common.Status;
import com.junbo.catalog.spec.model.promotion.PromotionType;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by lizwu on 2/14/14.
 */
public class PromotionDraftDaoTest extends BaseTest {
    @Autowired
    private PromotionDraftDao promotionDraftDao;

    @Test
    public void testCreatePromotion() {
        PromotionDraftEntity promotion = initPromotion();
        Long id = promotionDraftDao.create(promotion);

        Assert.assertNotNull(id);
        Assert.assertEquals(promotion.getId(), id);
    }
    @Test
    public void testGetPromotion() {
        PromotionDraftEntity promotion = initPromotion();
        promotionDraftDao.create(promotion);

        Assert.assertNotNull(promotionDraftDao.get(promotion.getId()));
    }

    @Test
    public void testUpdatePromotion() {
        PromotionDraftEntity promotion = initPromotion();
        promotionDraftDao.create(promotion);

        PromotionDraftEntity updatePromotion = promotionDraftDao.get(promotion.getId());
        updatePromotion.setName("20% off for cart");
        promotionDraftDao.update(updatePromotion);

        Assert.assertEquals(promotionDraftDao.get(promotion.getId()).getName(), updatePromotion.getName());
    }

    @Test
    public void testGetAllEffectivePromotions() {
        PromotionDraftEntity promotion = initPromotion();
        promotion.setStartDate(generateDate("2014-01-01 00:00:00"));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        promotion.setEndDate(calendar.getTime());
        promotionDraftDao.create(promotion);

        List<PromotionDraftEntity> promotions = promotionDraftDao.getEffectivePromotions(0,20);

        Assert.assertTrue(promotions.size() > 0);
    }

    private PromotionDraftEntity initPromotion() {
        PromotionDraftEntity promotion = new PromotionDraftEntity();
        promotion.setId(generateId());
        promotion.setName("10% off for cart");
        promotion.setType(PromotionType.ORDER_PROMOTION);
        promotion.setStatus(Status.RELEASED);
        promotion.setStartDate(generateDate("2013-01-01 08:00:00"));
        promotion.setEndDate(generateDate("2013-01-10 08:00:00"));
        promotion.setTimestamp(generateId());
        promotion.setPayload("{\"ruleId\":\"promotion001\"}");
        return promotion;
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
