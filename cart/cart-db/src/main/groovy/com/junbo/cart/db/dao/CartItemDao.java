/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.dao;

import com.junbo.cart.db.entity.CartItemEntity;
import com.junbo.cart.db.entity.ItemStatus;
import org.hibernate.Session;

import java.util.List;

/**
 * Created by fzhang@wan-san.com on 14-1-22.
 * @param <T> the type of cart item entity
 */
public interface CartItemDao<T extends CartItemEntity> {

    Session getSession();

    void insert(T item);

    void update(T item);

    T get(long id);

    List<T> getItems(long cartId, ItemStatus itemStatus);

    Class getEntityClass();
}
