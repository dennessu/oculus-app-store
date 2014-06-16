/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.auth

import com.junbo.authorization.AbstractAuthorizeCallback
import com.junbo.cart.spec.model.Cart
import com.junbo.common.id.UserId
import groovy.transform.CompileStatic

/**
 * CartAuthorizeCallback.
 */
@CompileStatic
class CartAuthorizeCallback extends AbstractAuthorizeCallback<Cart> {
    @Override
    String getApiName() {
        return 'carts'
    }

    CartAuthorizeCallback(CartAuthorizeCallbackFactory factory, Cart entity) {
        super(factory, entity)
    }

    @Override
    protected UserId getUserOwnerId() {
        Cart cart = getEntity()
        if (cart != null) {
            return cart.user as UserId
        }

        return null
    }
}
