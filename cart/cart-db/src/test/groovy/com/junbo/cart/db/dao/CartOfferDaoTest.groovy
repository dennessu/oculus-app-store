/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.dao

import com.junbo.cart.db.entity.ItemStatus
import com.junbo.common.id.CartId
import com.junbo.common.id.CartItemId
import org.testng.Assert
import org.testng.annotations.Test

import javax.annotation.Resource

/**
 * Created by fzhang@wan-san.com on 14-2-17.
 */
class CartOfferDaoTest extends DaoTestBase {

    @Resource(name='offerItemDao')
    private CartItemDao dao

    @Test
    void testInsertOffer() {
        def entity = testGenerator.offerItemEntity()
        entity.with {
            cartId = idGenerator.nextId(CartId)
            cartItemId = idGenerator.nextId(CartItemId, cartId)
        }

        dao.insert(entity)
        dao.getSession(entity.cartItemId).flush()
        Assert.assertSame(dao.get(entity.cartItemId), entity)
    }

    @Test
    void testUpdateOffer() {
        // insert
        def entity = testGenerator.offerItemEntity()
        entity.with {
            cartId = idGenerator.nextId(CartId)
            cartItemId = idGenerator.nextId(CartItemId, cartId)
        }

        dao.insert(entity)
        dao.getSession(entity.cartItemId).flush()

        // update
        def updatedEntity = testGenerator.offerItemEntity()
        updatedEntity.cartItemId = entity.cartItemId
        updatedEntity.cartId = entity.cartId
        dao.update(updatedEntity)
        dao.getSession(entity.cartItemId).flush()

        assert updatedEntity.cartItemId == entity.cartItemId
        assert updatedEntity.quantity == entity.quantity
        assert updatedEntity.isSelected == entity.isSelected
        assert updatedEntity.isApproved == entity.isApproved
        assert updatedEntity.offerId == entity.offerId
        assert updatedEntity.cartId == entity.cartId
        assert updatedEntity.properties == entity.properties
        assert updatedEntity.createdTime == entity.createdTime
        assert updatedEntity.updatedTime == entity.updatedTime
    }

    @Test
    void testGetItems() {
        long cartId = idGenerator.nextId(CartId)
        def status = [ItemStatus.OPEN, ItemStatus.DELETED, ItemStatus.DELETED, ItemStatus.OPEN, ItemStatus.OPEN]
        // insert
        status.each {
            def entity = testGenerator.offerItemEntity()
            entity.cartId = cartId
            entity.cartItemId = idGenerator.nextId(CartItemId, cartId)
            entity.status = it
            dao.insert(entity)
            dao.getSession(entity.cartItemId).flush()
        }

        assert dao.getItems(cartId, ItemStatus.DELETED).size() == 2
        assert dao.getItems(cartId, ItemStatus.OPEN).size() == 3
        assert dao.getItems(1L, ItemStatus.OPEN).size() == 0
    }
}

