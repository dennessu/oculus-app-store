/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.dao

import com.junbo.common.id.CartId
import com.junbo.common.id.CartItemId
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
        entity.with {
            cartId = idGenerator.nextId(CartId)
            cartItemId = idGenerator.nextId(CartItemId, cartId)
        }

        dao.insert(entity)
        dao.getSession(entity.cartItemId).flush()
        Assert.assertSame(dao.get(entity.cartItemId), entity)
    }

    @Test
    void testUpdateCoupon() {
        // insert
        def entity = testGenerator.couponItemEntity()
        entity.with {
            cartId = idGenerator.nextId(CartId)
            cartItemId = idGenerator.nextId(CartItemId, cartId)
        }
        dao.insert(entity)
        dao.getSession(entity.cartItemId).flush()

        // update
        def updatedEntity = testGenerator.couponItemEntity()
        updatedEntity.cartItemId = entity.cartItemId
        updatedEntity.cartId = entity.cartId
        dao.update(updatedEntity)
        dao.getSession(entity.cartItemId).flush()

        assert updatedEntity.cartItemId == entity.cartItemId
        assert updatedEntity.couponCode == entity.couponCode
        assert updatedEntity.cartId == entity.cartId
        assert updatedEntity.properties == entity.properties
        assert updatedEntity.createdTime == entity.createdTime
        assert updatedEntity.updatedTime == entity.updatedTime
    }
}
