package com.junbo.oauth.db.repo.impl

import com.junbo.oauth.db.dao.ResetPasswordCodeDAO
import com.junbo.oauth.db.entity.ResetPasswordCodeEntity
import com.junbo.oauth.db.generator.TokenGenerator
import com.junbo.oauth.db.repo.ResetPasswordCodeRepository
import com.junbo.oauth.spec.model.ResetPasswordCode
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by minhao on 5/2/14.
 */
@CompileStatic
class ResetPasswordCodeRepositoryImpl implements ResetPasswordCodeRepository {
    private ResetPasswordCodeDAO resetPasswordCodeDAO
    private TokenGenerator tokenGenerator

    @Required
    void setResetPasswordCodeDAO(ResetPasswordCodeDAO resetPasswordCodeDAO) {
        this.resetPasswordCodeDAO = resetPasswordCodeDAO
    }

    @Required
    void setTokenGenerator(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator
    }

    @Override
    ResetPasswordCode getAndRemove(String code) {
        ResetPasswordCodeEntity entity = resetPasswordCodeDAO.get(code)
        if (entity != null) {
            resetPasswordCodeDAO.delete(entity)
        }
        return wrap(entity)
    }

    @Override
    void save(ResetPasswordCode resetPasswordCode) {
        if (resetPasswordCode.code == null) {
            resetPasswordCode.code = tokenGenerator.generateEmailVerifyCode()
        }

        resetPasswordCodeDAO.save(unwrap(resetPasswordCode))
    }

    private static ResetPasswordCode wrap(ResetPasswordCodeEntity entity) {
        if (entity == null) {
            return null
        }

        return new ResetPasswordCode(
            code: entity.id,
            userId: entity.userId
        )
    }

    private static ResetPasswordCodeEntity unwrap(ResetPasswordCode entity) {
        if (entity == null) {
            return null
        }

        return new ResetPasswordCodeEntity(
            id: entity.code,
            userId: entity.userId
        )
    }
}