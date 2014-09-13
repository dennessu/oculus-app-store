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
    ResetPasswordCode get(String code) {
        ResetPasswordCodeEntity entity = resetPasswordCodeDAO.get(code)
        return wrap(entity)
    }

    @Override
    void remove(String code) {
        ResetPasswordCodeEntity entity = resetPasswordCodeDAO.get(code)
        if (entity != null) {
            resetPasswordCodeDAO.delete(entity)
        }
    }

    @Override
    void save(ResetPasswordCode resetPasswordCode) {
        if (resetPasswordCode.code == null) {
            resetPasswordCode.code = tokenGenerator.generateResetPasswordCode()
        }

        resetPasswordCodeDAO.save(unwrap(resetPasswordCode))
    }

    @Override
    void removeByUserIdEmail(Long userId, String email) {

    }

    private static ResetPasswordCode wrap(ResetPasswordCodeEntity entity) {
        if (entity == null) {
            return null
        }

        return new ResetPasswordCode(
            code: entity.id,
            userId: entity.userId,
            email: entity.email
        )
    }

    private static ResetPasswordCodeEntity unwrap(ResetPasswordCode entity) {
        if (entity == null) {
            return null
        }

        return new ResetPasswordCodeEntity(
            id: entity.code,
            userId: entity.userId,
            email: entity.email
        )
    }
}
