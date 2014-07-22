/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.dao.payment;

import com.junbo.payment.db.dao.GenericDAOImpl;
import com.junbo.payment.db.entity.payment.SettlementDetailEntity;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import java.util.List;


/**
 * Settlement Detail Dao.
 */
public class SettlementDetailDao extends GenericDAOImpl<SettlementDetailEntity, Long> {
    public SettlementDetailDao() {
        super(SettlementDetailEntity.class);
    }

    public void clearIfExists(Long batchIndex){
        String hql = "delete from SettlementDetailEntity where batch_index= :batchindex ";
        Query query = currentSession().createQuery(hql);
        query.setLong("batchindex", batchIndex);
        System.out.println(query.executeUpdate());
    }

    public List<SettlementDetailEntity> getByStatus(String status, int processCount){
        Criteria criteria = currentSession().createCriteria(SettlementDetailEntity.class);
        criteria.add(Restrictions.eq("status", status));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        criteria.setMaxResults(processCount);
        return criteria.list();
    }

}
