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
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by linyi on 14-2-8.
 */
@Repository("subledgerItemDao")
public class SubledgerItemDaoImpl extends BaseDaoImpl<SubledgerItemEntity> implements SubledgerItemDao {

    @Override
    @SuppressWarnings("unchecked")
    public List<SubledgerItemEntity> getByStatusOfferIdCreatedTime(Integer dataCenterId, Integer shardId, SubledgerItemStatus status,
                                                                   String offerId, Date endTime, int start, int count) {
        Criteria criteria = this.getSessionByShardId(dataCenterId, shardId).createCriteria(SubledgerItemEntity.class);

        criteria.add(Restrictions.eq("status", status));
        criteria.add(Restrictions.eq("offerId", offerId));
        criteria.add(Restrictions.lt("createdTime", endTime));

        criteria.setFirstResult(start);
        criteria.setMaxResults(count);

        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SubledgerItemEntity> getByOrderItemId(Long orderItemId) {
        Criteria criteria = this.getSession(orderItemId).createCriteria(SubledgerItemEntity.class);
        criteria.add(Restrictions.eq("orderItemId", orderItemId));
        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getDistrictOfferIds(Integer dataCenterId, Integer shardId, SubledgerItemStatus status, int start, int count) {
        Criteria criteria = this.getSessionByShardId(dataCenterId, shardId).createCriteria(SubledgerItemEntity.class);

        criteria.add(Restrictions.eq("status", status));
        criteria.setProjection(Projections.distinct(Projections.property("offerId")));

        criteria.setFirstResult(start);
        criteria.setMaxResults(count);
        return criteria.list();
    }
}
