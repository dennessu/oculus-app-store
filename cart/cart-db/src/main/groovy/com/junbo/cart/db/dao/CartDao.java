/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.dao;

import com.junbo.cart.db.entity.CartEntity;
import org.hibernate.Session;

/**
 * Created by fzhang@wan-san.com on 14-1-21.
 */
public interface CartDao {

    Session getSession(Object key);

    void insert(CartEntity cartEntity);

    CartEntity update(CartEntity cartEntity);

    CartEntity get(long id);

    CartEntity get(String clientId, String cartName, long userId);
}
