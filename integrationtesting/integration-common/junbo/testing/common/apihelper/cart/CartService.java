/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.testing.common.apihelper.cart;

import com.junbo.cart.spec.model.Cart;

/**
 * Created by jiefeng on 14-3-19.
 */
public interface CartService {
    //return cartId in blueprint;
    String  addCart(String userId, Cart cart) throws Exception;

    String addCart(String userId, Cart cart, int expectedResponseCode) throws Exception;

    String  getCart(String userId, String cartId) throws Exception;

    String  getCart(String userId, String cartId, int expectedResponseCode) throws Exception;

    String getCartPrimary(String userId) throws Exception;

    String getCartPrimary(String userId, int expectedResponseCode) throws Exception;

    String  getCartByName(String userId, String cartName) throws Exception;

    String  getCartByName(String userId, String cartName, int expectedResponseCode) throws Exception;

    String  updateCart(String userId, String cartId, Cart cart) throws Exception;

    String updateCart(String userId, String cartId, Cart cart, int expectedResponseCode) throws Exception;

    String  mergeCart(String userId, String cartId, Cart fromCart) throws Exception;

    String  mergeCart(String userId, String cartId, Cart fromCart, int expectedResponseCode) throws Exception;
}
