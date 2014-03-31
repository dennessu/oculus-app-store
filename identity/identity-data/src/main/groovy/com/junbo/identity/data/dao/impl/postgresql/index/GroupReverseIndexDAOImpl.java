/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql.index;

import com.junbo.identity.data.dao.impl.postgresql.ShardedDAOBase;
import com.junbo.identity.data.dao.index.GroupReverseIndexDAO;
import com.junbo.identity.data.entity.reverselookup.GroupReverseIndexEntity;
import com.junbo.sharding.annotations.SeedParam;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 * Created by liangfu on 3/21/14.
 */
public class GroupReverseIndexDAOImpl extends ShardedDAOBase implements GroupReverseIndexDAO {

    @Override
    public GroupReverseIndexEntity save(GroupReverseIndexEntity entity) {
        currentSession().save(entity);
        return get(entity.getName());
    }

    @Override
    public GroupReverseIndexEntity update(GroupReverseIndexEntity entity) {
        currentSession().merge(entity);
        currentSession().flush();

        return get(entity.getName());
    }

    @Override
    public GroupReverseIndexEntity get(@SeedParam String name) {
        Criteria criteria = currentSession().createCriteria(GroupReverseIndexEntity.class);
        criteria.add(Restrictions.eq("name", name));
        return criteria.list().size() == 0 ? null : (GroupReverseIndexEntity)criteria.list().get(0);
    }

    @Override
    public void delete(@SeedParam String name) {
        GroupReverseIndexEntity entity = get(name);
        currentSession().delete(entity);
    }
}
