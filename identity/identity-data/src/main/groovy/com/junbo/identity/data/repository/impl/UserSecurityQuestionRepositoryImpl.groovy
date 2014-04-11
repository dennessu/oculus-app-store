/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl

import com.junbo.common.id.UserSecurityQuestionId
import com.junbo.identity.data.dao.UserSecurityQuestionDAO
import com.junbo.identity.data.entity.user.UserSecurityQuestionEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserSecurityQuestionRepository
import com.junbo.identity.spec.model.users.UserSecurityQuestion
import com.junbo.identity.spec.options.list.UserSecurityQuestionListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
class UserSecurityQuestionRepositoryImpl implements UserSecurityQuestionRepository {
    @Autowired
    private UserSecurityQuestionDAO userSecurityQuestionDAO

    @Autowired
    private ModelMapper modelMapper

    @Override
    Promise<UserSecurityQuestion> create(UserSecurityQuestion entity) {
        UserSecurityQuestionEntity userSecurityQuestionEntity =
                modelMapper.toUserSecurityQuestion(entity, new MappingContext())
        userSecurityQuestionDAO.save(userSecurityQuestionEntity)

        return get(new UserSecurityQuestionId(userSecurityQuestionEntity.id))
    }

    @Override
    Promise<UserSecurityQuestion> update(UserSecurityQuestion entity) {
        UserSecurityQuestionEntity userSecurityQuestionEntity =
                modelMapper.toUserSecurityQuestion(entity, new MappingContext())
        userSecurityQuestionDAO.update(userSecurityQuestionEntity)

        return get((UserSecurityQuestionId)entity.id)
    }

    @Override
    Promise<UserSecurityQuestion> get(UserSecurityQuestionId id) {
        return Promise.pure(modelMapper.toUserSecurityQuestion(userSecurityQuestionDAO.get(id.value),
                new MappingContext()))
    }

    @Override
    Promise<List<UserSecurityQuestion>> search(UserSecurityQuestionListOptions getOption) {
        List entities = userSecurityQuestionDAO.search(getOption.userId.value, getOption)

        List<UserSecurityQuestion> results = new ArrayList<UserSecurityQuestion>()
        entities.each { UserSecurityQuestionEntity entity ->
            results.add(modelMapper.toUserSecurityQuestion(entity, new MappingContext()))
        }
        return Promise.pure(results)
    }

    @Override
    Promise<Void> delete(UserSecurityQuestionId id) {
        userSecurityQuestionDAO.delete(id.value)
        return Promise.pure(null)
    }
}
