/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.dao

import com.junbo.cart.db.entity.ItemStatus
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
        dao.insert(entity)
        dao.session.flush()
        Assert.assertSame(dao.get(entity.cartItemId), entity)
    }

    @Test
    void testUpdateOffer() {
        // insert
        def entity = testGenerator.offerItemEntity()
        dao.insert(entity)
        dao.session.flush()

        // update
        def updatedEntity = testGenerator.offerItemEntity()
        updatedEntity.cartItemId = entity.cartItemId
        dao.update(updatedEntity)
        dao.session.flush()

        assert updatedEntity.cartItemId == entity.cartItemId
        assert updatedEntity.quantity == entity.quantity
        assert updatedEntity.selected == entity.selected
        assert updatedEntity.offerId == entity.offerId
        assert updatedEntity.cartId == entity.cartId
        assert updatedEntity.properties == entity.properties
        assert updatedEntity.createdTime == entity.createdTime
        assert updatedEntity.updatedTime == entity.updatedTime
    }

    @Test
    void testGetItems() {
        long cartId = System.currentTimeMillis()
        def status = [ItemStatus.OPEN, ItemStatus.DELETED, ItemStatus.DELETED, ItemStatus.OPEN, ItemStatus.OPEN]
        // insert
        status.each {
            def entity = testGenerator.offerItemEntity()
            entity.cartId = cartId
            entity.status = it
            dao.insert(entity)
            dao.session.flush()
        }

        assert dao.getItems(cartId, ItemStatus.DELETED).size() == 2
        assert dao.getItems(cartId, ItemStatus.OPEN).size() == 3
        assert dao.getItems(1L, ItemStatus.OPEN).size() == 0
    }
}

