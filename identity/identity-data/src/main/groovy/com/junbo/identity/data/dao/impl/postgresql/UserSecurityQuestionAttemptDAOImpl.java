/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserSecurityQuestionAttemptDAO;
import com.junbo.identity.data.entity.user.UserSecurityQuestionAttemptEntity;
import com.junbo.identity.spec.options.list.UserSecurityQuestionAttemptListOptions;
import com.junbo.sharding.annotations.SeedParam;

import java.util.List;

/**
 * Created by liangfu on 3/25/14.
 */
public class UserSecurityQuestionAttemptDAOImpl extends ShardedDAOBase implements UserSecurityQuestionAttemptDAO {

    @Override
    public UserSecurityQuestionAttemptEntity save(UserSecurityQuestionAttemptEntity entity) {
        currentSession().save(entity);

        return get(entity.getId());
    }

    @Override
    public UserSecurityQuestionAttemptEntity update(UserSecurityQuestionAttemptEntity entity) {
        currentSession().merge(entity);
        currentSession().flush();

        return get(entity.getId());
    }

    @Override
    public UserSecurityQuestionAttemptEntity get(@SeedParam Long id) {
        return (UserSecurityQuestionAttemptEntity)currentSession().get(UserSecurityQuestionAttemptEntity.class, id);
    }

    @Override
    public List<UserSecurityQuestionAttemptEntity> search(@SeedParam Long userId,
                                                          UserSecurityQuestionAttemptListOptions getOption) {
        String query = "select * from user_security_question_attempt where user_id = " +
                (getOption.getUserId().getValue()) +
        (getOption.getSecurityQuestionId() == null ? "" : (" and security_question_id = " +
                getOption.getSecurityQuestionId().getValue())) +
        (" order by id limit " + (getOption.getLimit() == null ? "ALL" : getOption.getLimit().toString())) +
        " offset " + (getOption.getOffset() == null ? "0" : getOption.getOffset().toString());

        List entities = currentSession().createSQLQuery(query).
                addEntity(UserSecurityQuestionAttemptEntity.class).list();

        return entities;
    }
}
