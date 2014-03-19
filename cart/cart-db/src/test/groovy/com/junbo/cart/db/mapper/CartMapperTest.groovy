/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.mapper

import com.junbo.oom.core.MappingContext
import com.junbo.cart.db.util.Generator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

/**
 * Created by fzhang@wan-san.com on 14-2-15.
 */
@ContextConfiguration(locations = ['/context-test.xml'])
class CartMapperTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private CartMapper cartMapper

    def generator = new Generator()

    def context = new MappingContext()

    @Test
    void testToCartModel() {
        def entity = generator.cartEntity()
        def model = cartMapper.toCartModel(entity, context)
        assert model.user.value == entity.userId
        assert model.id.value == entity.id
        assert model.createdTime == entity.createdTime
        assert model.updatedTime == entity.updatedTime
        assert model.clientId == entity.clientId
        assert model.cartName == entity.cartName
        assert model.userLoggedIn == entity.userLoggedIn
    }

    @Test
    void testToCartEntity() {
        def model = generator.cart()
        def entity  = cartMapper.toCartEntity(model, context)
        assert model.user.value == entity.userId
        assert model.id.value == entity.id
        assert model.createdTime == entity.createdTime
        assert model.updatedTime == entity.updatedTime
        assert model.clientId == entity.clientId
        assert model.cartName == entity.cartName
        assert model.userLoggedIn == entity.userLoggedIn
    }

    @Test
    void testToOfferItemModel() {
        def entity = generator.offerItemEntity()
        def model = cartMapper.toOfferItemModel(entity, context)

        assert model.id.value == entity.cartItemId
        assert model.selected == entity.selected
        assert model.quantity == entity.quantity
        assert model.offer.value == entity.offerId
        assert model.createdTime == entity.createdTime
        assert model.updatedTime == entity.updatedTime
    }

    @Test
    void testToCouponItemModel() {
        def entity = generator.couponItemEntity()
        def model = cartMapper.toCouponItemModel(entity, context)

        assert model.id.value == entity.cartItemId
        assert model.coupon.value.toString() == entity.couponCode
        assert model.createdTime == entity.createdTime
        assert model.updatedTime == entity.updatedTime
    }
}


