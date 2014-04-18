/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl.sql

import com.junbo.common.id.UserSecurityQuestionVerifyAttemptId
import com.junbo.identity.data.dao.UserSecurityQuestionAttemptDAO
import com.junbo.identity.data.entity.user.UserSecurityQuestionAttemptEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserSecurityQuestionAttemptRepository
import com.junbo.identity.spec.v1.model.UserSecurityQuestionVerifyAttempt
import com.junbo.identity.spec.v1.option.list.UserSecurityQuestionAttemptListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by liangfu on 3/25/14.
 */
@CompileStatic
class UserSecurityQuestionAttemptRepositorySqlImpl implements UserSecurityQuestionAttemptRepository {

    @Autowired
    private UserSecurityQuestionAttemptDAO userSecurityQuestionAttemptDAO

    @Autowired
    private ModelMapper modelMapper

    @Override
    Promise<UserSecurityQuestionVerifyAttempt> create(UserSecurityQuestionVerifyAttempt entity) {
        UserSecurityQuestionAttemptEntity userSecurityQuestionAttemptEntity =
                modelMapper.toUserSecurityQuestionAttempt(entity, new MappingContext())

        userSecurityQuestionAttemptDAO.save(userSecurityQuestionAttemptEntity)

        return get(new UserSecurityQuestionVerifyAttemptId(userSecurityQuestionAttemptEntity.id))
    }

    @Override
    Promise<UserSecurityQuestionVerifyAttempt> get(UserSecurityQuestionVerifyAttemptId id) {
        UserSecurityQuestionAttemptEntity entity = userSecurityQuestionAttemptDAO.get(id.value)

        return Promise.pure(modelMapper.toUserSecurityQuestionAttempt(entity, new MappingContext()))
    }

    @Override
    Promise<UserSecurityQuestionVerifyAttempt> update(UserSecurityQuestionVerifyAttempt model) {
        throw new IllegalStateException('update user sec question attempt not support')
    }

    @Override
    Promise<Void> delete(UserSecurityQuestionVerifyAttemptId id) {
        throw new IllegalStateException('delete user sec question attempt not support')
    }

    @Override
    Promise<List<UserSecurityQuestionVerifyAttempt>> search(UserSecurityQuestionAttemptListOptions getOption) {
        List<UserSecurityQuestionAttemptEntity> entities =
                userSecurityQuestionAttemptDAO.search(getOption.userId.value, getOption)

        List<UserSecurityQuestionVerifyAttempt> results = new ArrayList<>()
        entities.each { UserSecurityQuestionAttemptEntity entity ->
            results.add(modelMapper.toUserSecurityQuestionAttempt(entity, new MappingContext()))
        }
        return Promise.pure(results)
    }
}
