/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.dao
import org.testng.Assert
import org.testng.annotations.Test

import javax.annotation.Resource
/**
 * Created by fzhang@wan-san.com on 14-2-17.
 */
class CartCouponDaoTest extends DaoTestBase {

    @Resource(name='couponItemDao')
    private CartItemDao dao

    @Test
    void testInsertCoupon() {
        def entity = testGenerator.couponItemEntity()
        dao.insert(entity)
        dao.session.flush()
        Assert.assertSame(dao.get(entity.cartItemId), entity)
    }

    @Test
    void testUpdateCoupon() {
        // insert
        def entity = testGenerator.couponItemEntity()
        dao.insert(entity)
        dao.session.flush()

        // update
        def updatedEntity = testGenerator.couponItemEntity()
        updatedEntity.cartItemId = entity.cartItemId
        dao.update(updatedEntity)
        dao.session.flush()

        assert updatedEntity.cartItemId == entity.cartItemId
        assert updatedEntity.couponCode == entity.couponCode
        assert updatedEntity.cartId == entity.cartId
        assert updatedEntity.properties == entity.properties
        assert updatedEntity.createdTime == entity.createdTime
        assert updatedEntity.updatedTime == entity.updatedTime
    }
}
