/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserSecurityQuestionDAO;
import com.junbo.identity.data.entity.user.UserSecurityQuestionEntity;
import com.junbo.identity.spec.options.list.UserSecurityQuestionListOptions;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
@Component
public class UserSecurityQuestionDAOImpl extends ShardedDAOBase implements UserSecurityQuestionDAO {
    @Override
    public UserSecurityQuestionEntity save(UserSecurityQuestionEntity entity) {
        currentSession().save(entity);

        return get(entity.getId());
    }

    @Override
    public UserSecurityQuestionEntity update(UserSecurityQuestionEntity entity) {
        currentSession().merge(entity);
        currentSession().flush();

        return get(entity.getId());
    }

    @Override
    public UserSecurityQuestionEntity get(Long id) {
        return (UserSecurityQuestionEntity)currentSession().get(UserSecurityQuestionEntity.class, id);
    }

    @Override
    public List<UserSecurityQuestionEntity> search(Long userId, UserSecurityQuestionListOptions getOption) {
        String query = "select * from user_security_question where user_id = " + getOption.getUserId().getValue() +
            (getOption.getSecurityQuestionId() == null ? "" :
                    (" and security_question_id = " + getOption.getSecurityQuestionId().getValue())) +
            (" order by id limit " + (getOption.getLimit() == null ? "ALL" : getOption.getLimit().toString())) +
            " offset " + (getOption.getOffset() == null ? "0" : getOption.getOffset().toString());

        List entities = currentSession().createSQLQuery(query).addEntity(UserSecurityQuestionEntity.class).list();

        return entities;
    }

    @Override
    public void delete(Long id) {
        UserSecurityQuestionEntity entity = (UserSecurityQuestionEntity)currentSession().
                get(UserSecurityQuestionEntity.class, id);
        currentSession().delete(entity);
    }
}
