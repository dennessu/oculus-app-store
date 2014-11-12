/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.SubledgerEventDao;
import com.junbo.order.db.entity.SubledgerEventEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by LinYi on 2/8/14.
 */
@Repository("subledgerEventDao")
public class SubledgerEventDaoImpl extends BaseDaoImpl<SubledgerEventEntity> implements SubledgerEventDao {

    @Override
    @SuppressWarnings("unchecked")
    public List<SubledgerEventEntity> getSubledgerEventsBySubledgerId(Long subledgerId) {
        Criteria criteria = this.getSession(subledgerId).createCriteria(SubledgerEventEntity.class);

        criteria.add(Restrictions.eq("subledgerId", subledgerId));
        return criteria.list();
    }


}
