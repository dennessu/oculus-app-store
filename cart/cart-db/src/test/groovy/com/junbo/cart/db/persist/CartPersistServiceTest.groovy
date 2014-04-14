/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.persist
import com.junbo.cart.core.service.CartPersistService
import com.junbo.cart.db.dao.DaoTestBase
import com.junbo.cart.spec.model.item.OfferItem
import com.junbo.common.id.OfferId
import com.junbo.common.id.UserId
import org.apache.commons.lang.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.testng.annotations.Test
/**
 * Created by fzhang@wan-san.com on 14-2-17.
 */
class CartPersistServiceTest extends DaoTestBase {

    @Autowired
    private CartPersistService service

    @Test
    void testGetCart() {
        def cart = testGenerator.cart()
        cart.user = new UserId(idGenerator.nextId(UserId))
        cart.couponCodes = [RandomStringUtils.randomAlphabetic(5)]
        cart.offers = [new OfferItem(
                offer: new OfferId(idGenerator.nextId(OfferId)),
                quantity: 1,
                selected: true
        )]

        service.saveNewCart(cart)
        def read = service.getCart(cart.getId(), true)
        assert read.couponCodes[0] == cart.couponCodes[0]
        assert read.offers[0].offer == cart.offers[0].offer
    }
}
