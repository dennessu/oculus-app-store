/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.SecurityQuestionDAO;
import com.junbo.identity.data.entity.domaindata.SecurityQuestionEntity;
import com.junbo.identity.spec.model.options.DomainDataGetOption;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by liangfu on 3/16/14.
 */
@Component
public class SecurityQuestionDAOImpl extends EntityDAOImpl implements SecurityQuestionDAO {

    @Override
    public SecurityQuestionEntity save(SecurityQuestionEntity entity) {
        currentSession().save(entity);

        return get(entity.getId());
    }

    @Override
    public SecurityQuestionEntity update(SecurityQuestionEntity entity) {
        currentSession().merge(entity);
        currentSession().flush();

        return get(entity.getId());
    }

    @Override
    public SecurityQuestionEntity get(Long id) {
        return (SecurityQuestionEntity)currentSession().get(SecurityQuestionEntity.class, id);
    }

    @Override
    public List<SecurityQuestionEntity> search(DomainDataGetOption getOption) {
        String query = "select * from security_question where value like \'%" + getOption.getValue() + "%\'" +
                (" order by id limit " + (getOption.getLimit() == null ? "ALL" : getOption.getLimit().toString())) +
                " offset " + (getOption.getOffset() == null ? "0" : getOption.getOffset().toString());
        List entities = currentSession().createSQLQuery(query).addEntity(SecurityQuestionEntity.class).list();

        return entities;
    }

    @Override
    public void delete(Long id) {
        SecurityQuestionEntity entity = (SecurityQuestionEntity)currentSession().get(SecurityQuestionEntity.class, id);
        currentSession().delete(entity);
    }
}
