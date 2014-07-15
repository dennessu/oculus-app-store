/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.OrderItemPreorderUpdateHistoryDao;
import com.junbo.order.db.entity.OrderItemPreorderUpdateHistoryEntity;
import com.junbo.sharding.ShardAlgorithm;
import com.junbo.sharding.hibernate.ShardScope;
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

    @Autowired
    @Qualifier("userShardAlgorithm")
    private ShardAlgorithm shardAlgorithm;

    protected Session getSession(Object key) {
        ShardScope shardScope = new ShardScope(shardAlgorithm.dataCenterId(key), shardAlgorithm.shardId(key));
        try {
            return sessionFactory.getCurrentSession();
        } finally {
            shardScope.close();
        }
    }

    public Long create(OrderItemPreorderUpdateHistoryEntity entity) {
        Session session = this.getSession(entity.getOrderItemPreorderUpdateHistoryId());
        Long id = (Long) session.save(entity);
        session.flush();
        return id;
    }

    public OrderItemPreorderUpdateHistoryEntity read(long id) {
        return (OrderItemPreorderUpdateHistoryEntity) this.getSession(id).
                get(OrderItemPreorderUpdateHistoryEntity.class, id);
    }

    public void update(OrderItemPreorderUpdateHistoryEntity entity) {
        Session session = this.getSession(entity.getOrderItemPreorderUpdateHistoryId());
        session.merge(entity);
        session.flush();
    }
}
