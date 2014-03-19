/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.OrderItemPreorderUpdateHistoryDao;
import com.junbo.order.db.entity.OrderItemPreorderUpdateHistoryEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * Created by LinYi on 2/8/14.
 */
@Repository("orderItemPreorderUpdateHistoryDao")
public class OrderItemPreorderUpdateHistoryDaoImpl implements OrderItemPreorderUpdateHistoryDao {
    @Autowired
    @Qualifier("orderSessionFactory")
    private SessionFactory sessionFactory;

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public Long create(OrderItemPreorderUpdateHistoryEntity entity) {
        return (Long) this.getSession().save(entity);
    }

    public OrderItemPreorderUpdateHistoryEntity read(long id) {
        return (OrderItemPreorderUpdateHistoryEntity) this.getSession().
                get(OrderItemPreorderUpdateHistoryEntity.class, id);
    }

    public void update(OrderItemPreorderUpdateHistoryEntity entity) {
        this.getSession().update(entity);
    }

    public void flush() {
        this.getSession().flush();
    }
}
