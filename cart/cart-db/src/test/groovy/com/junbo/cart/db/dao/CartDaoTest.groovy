/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.dao

import com.junbo.cart.db.entity.CartEntity
import com.junbo.common.id.CartId
import com.junbo.common.id.UserId
import org.springframework.beans.factory.annotation.Autowired
import org.testng.Assert
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

/**
 * Created by fzhang@wan-san.com on 14-2-15.
 */
class CartDaoTest  extends DaoTestBase {

    @Autowired
    private CartDao cartDao

    private long userId

    @BeforeMethod
    void setup() {
        userId = idGenerator.nextId(UserId)
    }

    @Test(enabled = false)
    void testCartInsert() {
        CartEntity cartEntity = testGenerator.cartEntity()
        cartEntity.userId = userId
        cartEntity.id = idGenerator.nextId(CartId, userId)

        cartDao.insert(cartEntity)
        cartDao.getSession(cartEntity.userId).flush()
        Assert.assertSame(cartDao.get(cartEntity.id), cartEntity)
        Assert.assertSame(cartDao.get(cartEntity.clientId, cartEntity.cartName, cartEntity.userId),
             cartEntity)
    }

    @Test(enabled = false)
    void testCartUpdate() {
        // insert
        CartEntity cartEntity = testGenerator.cartEntity()
        cartEntity.userId = userId
        cartEntity.id = idGenerator.nextId(CartId, userId)

        cartDao.insert(cartEntity)
        cartDao.getSession(cartEntity.userId).flush()
        def oldClientId = cartEntity.clientId
        def oldCartName = cartEntity.cartName
        def oldUserId = cartEntity.userId

        // update
        CartEntity updatedEntity = testGenerator.cartEntity()
        updatedEntity.resourceAge = cartEntity.resourceAge
        updatedEntity.id = cartEntity.id
        updatedEntity.userId = cartEntity.userId
        cartDao.update(updatedEntity)
        cartDao.getSession(cartEntity.userId).flush()

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

    @Test(enabled = false)
    void testCartGetById() {
        CartEntity cartEntity = testGenerator.cartEntity()
        cartEntity.userId = userId
        cartEntity.id = idGenerator.nextId(CartId, userId)

        cartDao.insert(cartEntity)
        cartDao.getSession(cartEntity.userId).flush()
        Assert.assertSame(cartDao.get(cartEntity.id), cartEntity)
        assert cartDao.get(idGenerator.nextId(CartId, userId)) == null
    }

    @Test(enabled = false)
    void testCartGetByUser() {
        CartEntity cartEntity = testGenerator.cartEntity()
        cartEntity.userId = userId
        cartEntity.id = idGenerator.nextId(CartId, userId)

        cartDao.insert(cartEntity)
        cartDao.getSession(cartEntity.userId).flush()
        Assert.assertSame(cartDao.get(cartEntity.id), cartEntity)
        Assert.assertSame(cartDao.get(cartEntity.clientId, cartEntity.cartName, cartEntity.userId),
                cartEntity)
        assert cartDao.get(cartEntity.clientId, '', cartEntity.userId) == null
    }
}


