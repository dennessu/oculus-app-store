/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.persist
import com.junbo.cart.core.service.CartPersistService
import com.junbo.cart.db.dao.DaoTestBase
import com.junbo.cart.spec.model.item.OfferItem
import com.junbo.common.id.CouponId
import com.junbo.common.id.OfferId
import com.junbo.common.id.UserId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.testng.annotations.Test
/**
 * Created by fzhang@wan-san.com on 14-2-17.
 */
class CartPersistServiceTest extends DaoTestBase {

    @Autowired
    @Qualifier("cartPersistService")
    private CartPersistService service

    @Test
    void testGetCart() {
        def cart = testGenerator.cart()
        cart.user = new UserId(idGenerator.nextId(UserId))
        cart.coupons = [new CouponId(idGenerator.nextId(CouponId))]
        cart.offers = [new OfferItem(
                offer: new OfferId(idGenerator.nextId(OfferId).toString()),
                quantity: 1,
                isSelected: true,
                isApproved: true
        )]

        cart = service.create(cart).get()
        cart = service.get(cart.clientId, cart.cartName, cart.user).get();
        def read = service.get(cart.getId()).get()
        assert read.coupons[0] == cart.coupons[0]
        assert read.offers[0].offer == cart.offers[0].offer
    }
}
