/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.core.service;

import com.junbo.cart.spec.model.Cart;
import com.junbo.common.id.CartId;
import com.junbo.common.id.UserId;
import com.junbo.langur.core.promise.Promise;

/**
 * Created by fzhang@wan-san.com on 14-1-28.
 */
public interface CartPersistService {

    Promise<Cart> get(CartId cartId);

    Promise<Cart> get(String clientId, String cartName, UserId userId);

    Promise<Cart> create(Cart cart);

    Promise<Cart> update(Cart cart, Cart oldCart);
}
