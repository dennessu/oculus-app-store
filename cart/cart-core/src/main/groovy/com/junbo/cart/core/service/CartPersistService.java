/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.core.service;

import com.junbo.cart.spec.model.Cart;
import com.junbo.common.id.CartId;
import com.junbo.common.id.UserId;

/**
 * Created by fzhang@wan-san.com on 14-1-28.
 */
public interface CartPersistService {

    Cart getCart(CartId cartId, boolean includeItems);

    Cart getCart(String clientId, String cartName, UserId userId, boolean includeItems);

    void saveNewCart(Cart newCart);

    void updateCart(Cart cart);
}
