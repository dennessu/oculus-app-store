/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.dao

import com.junbo.cart.db.entity.CartEntity
import org.springframework.beans.factory.annotation.Autowired
import org.testng.Assert
import org.testng.annotations.Test

/**
 * Created by fzhang@wan-san.com on 14-2-15.
 */
class CartDaoTest  extends DaoTestBase {

    @Autowired
    private CartDao cartDao

    @Test
    void testCartInsert() {
        CartEntity cartEntity = testGenerator.cartEntity()
        cartDao.insert(cartEntity)
        cartDao.session.flush()
        Assert.assertSame(cartDao.get(cartEntity.id), cartEntity)
        Assert.assertSame(cartDao.get(cartEntity.clientId, cartEntity.cartName, cartEntity.userId),
             cartEntity)
    }

    @Test
    void testCartUpdate() {
        // insert
        CartEntity cartEntity = testGenerator.cartEntity()
        cartDao.insert(cartEntity)
        cartDao.session.flush()
        def oldClientId = cartEntity.clientId
        def oldCartName = cartEntity.cartName
        def oldUserId = cartEntity.userId

        // update
        CartEntity updatedEntity = testGenerator.cartEntity()
        updatedEntity.resourceAge = cartEntity.resourceAge
        updatedEntity.id = cartEntity.id
        cartDao.update(updatedEntity)
        cartDao.session.flush()

        Assert.assertSame(cartDao.get(updatedEntity.clientId, updatedEntity.cartName, updatedEntity.userId),
                cartEntity)
        assert cartDao.get(oldClientId, oldCartName, oldUserId) == null
        assert cartEntity.clientId == updatedEntity.clientId
        assert cartEntity.cartName == updatedEntity.cartName
        assert cartEntity.resourceAge == updatedEntity.resourceAge + 1
        assert cartEntity.properties == updatedEntity.properties
        assert cartEntity.userId == updatedEntity.userId
        assert cartEntity.userLoggedIn == updatedEntity.userLoggedIn
        assert cartEntity.createdTime == updatedEntity.createdTime
        assert cartEntity.updatedTime == updatedEntity.updatedTime
    }

    @Test
    void testCartGetById() {
        CartEntity cartEntity = testGenerator.cartEntity()
        cartDao.insert(cartEntity)
        cartDao.session.flush()
        Assert.assertSame(cartDao.get(cartEntity.id), cartEntity)
        assert cartDao.get(-1) == null
    }

    @Test
    void testCartGetByUser() {
        CartEntity cartEntity = testGenerator.cartEntity()
        cartDao.insert(cartEntity)
        cartDao.session.flush()
        Assert.assertSame(cartDao.get(cartEntity.id), cartEntity)
        Assert.assertSame(cartDao.get(cartEntity.clientId, cartEntity.cartName, cartEntity.userId),
                cartEntity)
        assert cartDao.get(cartEntity.clientId, '', cartEntity.userId) == null
    }
}


