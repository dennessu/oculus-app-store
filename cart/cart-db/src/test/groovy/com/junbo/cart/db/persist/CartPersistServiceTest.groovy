/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.persist

import com.junbo.oom.core.MappingContext
import com.junbo.cart.db.dao.CartDao
import com.junbo.cart.db.dao.CartItemDao
import com.junbo.cart.db.entity.ItemStatus
import com.junbo.cart.db.mapper.CartMapper
import com.junbo.cart.db.util.Generator
import com.junbo.cart.spec.model.Cart

import com.junbo.cart.spec.model.item.OfferItem
import com.junbo.common.id.CartId
import org.easymock.EasyMock
import org.testng.Assert
import org.testng.annotations.Test

/**
 * Created by fzhang@wan-san.com on 14-2-17.
 */
class CartPersistServiceTest {

    def service = new CartPersistServiceImpl()

    def cartDao = EasyMock.createMock(CartDao)

    def offerItemDao = EasyMock.createMock(CartItemDao)

    def couponItemDao = EasyMock.createMock(CartItemDao)

    def dataMapper = EasyMock.createMock(CartMapper)

    def generator = new Generator()

    private void setup() {
        EasyMock.reset(cartDao, offerItemDao, couponItemDao, dataMapper)
        service.offerItemDao = offerItemDao
        service.couponItemDao = couponItemDao
        service.dataMapper = dataMapper
        service.cartDao = cartDao
    }

    @Test
    void testGetCart() {
        setup()
        def cart = new Cart()
        def cartEntity = generator.cartEntity()
        def offers = [generator.offerItemEntity()]
        def coupons = [generator.couponItemEntity()]

        EasyMock.expect(cartDao.get(cartEntity.id)).andReturn(cartEntity)
        EasyMock.expect(dataMapper.toCartModel(EasyMock.same(cartEntity), EasyMock.isA(MappingContext))).andReturn(cart)
        EasyMock.expect(offerItemDao.getItems(cartEntity.id, ItemStatus.OPEN)).andReturn(offers)
        EasyMock.expect(couponItemDao.getItems(cartEntity.id, ItemStatus.OPEN)).andReturn(coupons)
        offers.each {
            EasyMock.expect(dataMapper.toOfferItemModel(EasyMock.same(it), EasyMock.isA(MappingContext))).
                    andReturn(new OfferItem())
        }

        replay()
        Assert.assertSame(service.getCart(new CartId(cartEntity.id), true), cart)
        assert cart.offers.size() == 1
        assert cart.couponCodes.size() == 1
        verify()
    }

    private void replay() {
        EasyMock.replay(cartDao, offerItemDao, couponItemDao, dataMapper)
    }

    private void verify() {
        EasyMock.verify(cartDao, offerItemDao, couponItemDao, dataMapper)
    }
}
