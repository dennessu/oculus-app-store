/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.auth

import com.junbo.authorization.AbstractAuthorizeCallbackFactory
import com.junbo.authorization.AuthorizeCallback
import com.junbo.cart.spec.model.Cart
import com.junbo.common.id.UserId
import groovy.transform.CompileStatic

/**
 * CartAuthorizeCallbackFactory.
 */
@CompileStatic
class CartAuthorizeCallbackFactory extends AbstractAuthorizeCallbackFactory<Cart> {
    @Override
    AuthorizeCallback<Cart> create(Cart entity) {
        return new CartAuthorizeCallback(this, entity)
    }

    AuthorizeCallback<Cart> create(UserId userId) {
        return create(new Cart(user:userId))
    }
}
