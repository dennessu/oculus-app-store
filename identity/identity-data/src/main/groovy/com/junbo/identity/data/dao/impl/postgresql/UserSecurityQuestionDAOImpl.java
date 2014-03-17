/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.common.id.UserSecurityQuestionId;
import com.junbo.identity.data.dao.UserSecurityQuestionDAO;
import com.junbo.identity.data.entity.user.UserSecurityQuestionEntity;
import com.junbo.identity.data.mapper.ModelMapper;
import com.junbo.identity.spec.model.options.UserSecurityQuestionGetOption;
import com.junbo.identity.spec.model.users.UserSecurityQuestion;
import com.junbo.oom.core.MappingContext;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
@Component
public class UserSecurityQuestionDAOImpl implements UserSecurityQuestionDAO {
    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

    @Autowired
    private ModelMapper modelMapper;

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public UserSecurityQuestion save(UserSecurityQuestion entity) {
        UserSecurityQuestionEntity userSecurityQuestionEntity =
                modelMapper.toUserSecurityQuestion(entity, new MappingContext());
        currentSession().save(userSecurityQuestionEntity);

        return get(entity.getId());
    }

    @Override
    public UserSecurityQuestion update(UserSecurityQuestion entity) {
        UserSecurityQuestionEntity userSecurityQuestionEntity =
                modelMapper.toUserSecurityQuestion(entity, new MappingContext());
        currentSession().merge(userSecurityQuestionEntity);

        return get(entity.getId());
    }

    @Override
    public UserSecurityQuestion get(UserSecurityQuestionId id) {
        return modelMapper.toUserSecurityQuestion((UserSecurityQuestionEntity)currentSession().
                get(UserSecurityQuestionEntity.class, id.getValue()), new MappingContext());
    }

    @Override
    public List<UserSecurityQuestion> search(UserSecurityQuestionGetOption getOption) {
        String query = "select * from user_security_question where user_id = " + getOption.getUserId().getValue() +
            (getOption.getSecurityQuestionId() == null ? "" :
                    (" and security_question_id = " + getOption.getSecurityQuestionId().getValue())) +
            (" order by id limit " + (getOption.getLimit() == null ? "ALL" : getOption.getLimit().toString())) +
            " offset " + (getOption.getOffset() == null ? "0" : getOption.getOffset().toString());

        List entities = currentSession().createSQLQuery(query).addEntity(UserSecurityQuestionEntity.class).list();

        List<UserSecurityQuestion> results = new ArrayList<UserSecurityQuestion>();
        for(int i =0 ; i< entities.size(); i++) {
            results.add(modelMapper.toUserSecurityQuestion((UserSecurityQuestionEntity)entities.get(i),
                    new MappingContext()));
        }
        return results;
    }

    @Override
    public void delete(UserSecurityQuestionId id) {
        UserSecurityQuestionEntity entity = (UserSecurityQuestionEntity)currentSession().
                get(UserSecurityQuestionEntity.class, id.getValue());
        currentSession().delete(entity);
    }
}
