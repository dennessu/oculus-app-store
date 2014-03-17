/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.common.id.SecurityQuestionId;
import com.junbo.identity.data.dao.SecurityQuestionDAO;
import com.junbo.identity.data.entity.domaindata.SecurityQuestionEntity;
import com.junbo.identity.data.mapper.ModelMapper;
import com.junbo.identity.spec.model.domaindata.SecurityQuestion;
import com.junbo.identity.spec.model.options.DomainDataGetOption;
import com.junbo.oom.core.MappingContext;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangfu on 3/16/14.
 */
@Component
public class SecurityQuestionDAOImpl implements SecurityQuestionDAO {
    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public SecurityQuestion save(SecurityQuestion entity) {
        SecurityQuestionEntity securityQuestionEntity = modelMapper.toSecurityQuestion(entity, new MappingContext());
        sessionFactory.getCurrentSession().save(securityQuestionEntity);

        return get(entity.getId());
    }

    @Override
    public SecurityQuestion update(SecurityQuestion entity) {
        SecurityQuestionEntity securityQuestionEntity = modelMapper.toSecurityQuestion(entity, new MappingContext());

        sessionFactory.getCurrentSession().merge(securityQuestionEntity);

        return get(entity.getId());
    }

    @Override
    public SecurityQuestion get(SecurityQuestionId id) {
        return modelMapper.toSecurityQuestion((SecurityQuestionEntity)sessionFactory.getCurrentSession().
                get(SecurityQuestionEntity.class, id.getValue()), new MappingContext());
    }

    @Override
    public List<SecurityQuestion> search(DomainDataGetOption getOption) {
        String query = "select * from security_question where value like \'%" + getOption.getValue() + "%\'" +
                (" order by id limit " + (getOption.getLimit() == null ? "ALL" : getOption.getLimit().toString())) +
                " offset " + (getOption.getOffset() == null ? "0" : getOption.getOffset().toString());
        List entities = sessionFactory.getCurrentSession().createSQLQuery(query)
                .addEntity(SecurityQuestionEntity.class).list();

        List<SecurityQuestion> results = new ArrayList<SecurityQuestion>();
        for(int i =0 ; i< entities.size(); i++) {
            results.add(modelMapper.toSecurityQuestion((SecurityQuestionEntity) entities.get(i), new MappingContext()));
        }
        return results;
    }

    @Override
    public void delete(SecurityQuestionId id) {
        SecurityQuestionEntity entity = (SecurityQuestionEntity)sessionFactory.getCurrentSession()
                .get(SecurityQuestionEntity.class, id.getValue());
        sessionFactory.getCurrentSession().delete(entity);
    }
}
