/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.impl

import com.junbo.oauth.db.dao.EmailVerifyCodeDAO
import com.junbo.oauth.db.entity.EmailVerifyCodeEntity
import com.junbo.oauth.db.generator.TokenGenerator
import com.junbo.oauth.db.repo.EmailVerifyCodeRepository
import com.junbo.oauth.spec.model.EmailVerifyCode
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * EmailVerifyCodeRepositoryImpl.
 */
@CompileStatic
class EmailVerifyCodeRepositoryImpl implements EmailVerifyCodeRepository {
    private EmailVerifyCodeDAO emailVerifyCodeDAO

    private TokenGenerator tokenGenerator

    @Required
    void setEmailVerifyCodeDAO(EmailVerifyCodeDAO emailVerifyCodeDAO) {
        this.emailVerifyCodeDAO = emailVerifyCodeDAO
    }

    @Required
    void setTokenGenerator(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator
    }

    @Override
    EmailVerifyCode getAndRemove(String code) {
        EmailVerifyCodeEntity entity = emailVerifyCodeDAO.get(code)
        if (entity != null) {
            emailVerifyCodeDAO.delete(entity)
        }
        return wrap(entity)
    }

    @Override
    void save(EmailVerifyCode emailVerifyCode) {
        if (emailVerifyCode.code == null) {
            emailVerifyCode.code = tokenGenerator.generateEmailVerifyCode()
        }

        emailVerifyCodeDAO.save(unwrap(emailVerifyCode))
    }

    @Override
    void removeByUserIdEmail(Long userId, String email) {

    }

    @Override
    List<String> getByUserIdEmail(Long userId, String email) {
        return new ArrayList<String>()
    }

    private static EmailVerifyCode wrap(EmailVerifyCodeEntity entity) {
        if (entity == null) {
            return null
        }

        return new EmailVerifyCode(
                code: entity.id,
                email: entity.email,
                userId: entity.userId,
                expiredBy: entity.expiredBy
        )
    }

    private static EmailVerifyCodeEntity unwrap(EmailVerifyCode entity) {
        if (entity == null) {
            return null
        }

        return new EmailVerifyCodeEntity(
                id: entity.code,
                email: entity.email,
                userId: entity.userId,
                expiredBy: entity.expiredBy
        )
    }
}
