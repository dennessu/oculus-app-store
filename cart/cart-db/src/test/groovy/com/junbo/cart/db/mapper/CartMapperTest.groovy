/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.mapper

import com.junbo.cart.db.dao.DaoTestBase
import com.junbo.cart.db.util.Generator
import com.junbo.oom.core.MappingContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.testng.annotations.Test

/**
 * Created by fzhang@wan-san.com on 14-2-15.
 */
class CartMapperTest extends DaoTestBase {

    @Autowired(required = false)
    @Qualifier('dataMapper')
    private CartMapper cartMapper

    def generator = new Generator()

    def context = new MappingContext()

    @Test(enabled = false)
    void testToCartModel() {
        def entity = generator.cartEntity()
        def model = cartMapper.toCartModel(entity, context)
        assert model.user.value == entity.userId
        assert model.id.value == entity.id.toString()
        assert model.createdTime == entity.createdTime
        assert model.updatedTime == entity.updatedTime
        assert model.clientId == entity.clientId
        assert model.cartName == entity.cartName
        assert model.userLoggedIn == entity.userLoggedIn
    }

    @Test(enabled = false)
    void testToCartEntity() {
        def model = generator.cart()
        def entity  = cartMapper.toCartEntity(model, context)
        assert model.user.value == entity.userId
        assert model.id.value == entity.id.toString()
        assert model.createdTime == entity.createdTime
        assert model.updatedTime == entity.updatedTime
        assert model.clientId == entity.clientId
        assert model.cartName == entity.cartName
        assert model.userLoggedIn == entity.userLoggedIn
    }

    @Test(enabled = false)
    void testToOfferItemModel() {
        def entity = generator.offerItemEntity()
        def model = cartMapper.toOfferItemModel(entity, context)

        assert model.id.value == entity.cartItemId.toString()
        assert model.isSelected == entity.isSelected
        assert model.quantity == entity.quantity
        assert model.offer.value == entity.offerId
    }

}


