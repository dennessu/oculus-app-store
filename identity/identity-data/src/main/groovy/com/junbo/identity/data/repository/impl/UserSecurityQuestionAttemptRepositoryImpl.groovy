/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl

import com.junbo.common.id.UserSecurityQuestionVerifyAttemptId
import com.junbo.identity.data.dao.UserSecurityQuestionAttemptDAO
import com.junbo.identity.data.entity.user.UserSecurityQuestionAttemptEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserSecurityQuestionAttemptRepository
import com.junbo.identity.spec.model.users.UserSecurityQuestionAttempt
import com.junbo.identity.spec.options.list.UserSecurityQuestionAttemptListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by liangfu on 3/25/14.
 */
@CompileStatic
class UserSecurityQuestionAttemptRepositoryImpl implements UserSecurityQuestionAttemptRepository {

    @Autowired
    private UserSecurityQuestionAttemptDAO userSecurityQuestionAttemptDAO

    @Autowired
    private ModelMapper modelMapper

    @Override
    Promise<UserSecurityQuestionAttempt> create(UserSecurityQuestionAttempt entity) {
        UserSecurityQuestionAttemptEntity userSecurityQuestionAttemptEntity =
                modelMapper.toUserSecurityQuestionAttempt(entity, new MappingContext())

        userSecurityQuestionAttemptDAO.save(userSecurityQuestionAttemptEntity)

        return get(new UserSecurityQuestionVerifyAttemptId(userSecurityQuestionAttemptEntity.id))
    }

    @Override
    Promise<UserSecurityQuestionAttempt> update(UserSecurityQuestionAttempt entity) {
        UserSecurityQuestionAttemptEntity userSecurityQuestionAttemptEntity =
                modelMapper.toUserSecurityQuestionAttempt(entity, new MappingContext())

        userSecurityQuestionAttemptDAO.update(userSecurityQuestionAttemptEntity)

        return get((UserSecurityQuestionVerifyAttemptId)entity.id)
    }

    @Override
    Promise<UserSecurityQuestionAttempt> get(UserSecurityQuestionVerifyAttemptId id) {
        return Promise.pure(modelMapper.toUserSecurityQuestionAttempt(userSecurityQuestionAttemptDAO.get(id.value),
                new MappingContext()))
    }

    @Override
    Promise<List<UserSecurityQuestionAttempt>> search(UserSecurityQuestionAttemptListOptions getOption) {
        List<UserSecurityQuestionAttemptEntity> entities =
                userSecurityQuestionAttemptDAO.search(getOption.userId.value, getOption)

        List<UserSecurityQuestionAttempt> results = new ArrayList<>()
        entities.each { UserSecurityQuestionAttemptEntity entity ->
            results.add(modelMapper.toUserSecurityQuestionAttempt(entity, new MappingContext()))
        }
        return Promise.pure(results)
    }
}
