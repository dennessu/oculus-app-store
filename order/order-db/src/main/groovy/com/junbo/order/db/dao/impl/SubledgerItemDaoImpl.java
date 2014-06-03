/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.SubledgerItemDao;
import com.junbo.order.db.entity.SubledgerItemEntity;
import com.junbo.order.spec.model.enums.SubledgerItemStatus;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by linyi on 14-2-8.
 */
@Repository("subledgerItemDao")
public class SubledgerItemDaoImpl extends BaseDaoImpl<SubledgerItemEntity> implements SubledgerItemDao {
    @Override
    @SuppressWarnings("unchecked")
    public List<SubledgerItemEntity> getByStatus(Integer shardId, SubledgerItemStatus status, int start, int count) {
        Criteria criteria = this.getSessionByShardId(shardId).createCriteria(SubledgerItemEntity.class);

        criteria.add(Restrictions.eq("status", status));

        criteria.setFirstResult(start);
        criteria.setMaxResults(count);

        return criteria.list();
    }
}
