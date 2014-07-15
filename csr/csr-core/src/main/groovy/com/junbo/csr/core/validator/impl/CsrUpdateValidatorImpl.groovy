package com.junbo.csr.core.validator.impl

import com.junbo.common.id.CsrUpdateId
import com.junbo.csr.core.validator.CsrUpdateValidator
import com.junbo.csr.db.repo.CsrUpdateRepository
import com.junbo.csr.spec.error.AppErrors
import com.junbo.csr.spec.model.CsrUpdate
import com.junbo.csr.spec.option.list.CsrUpdateListOptions
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-7-4.
 */
@CompileStatic
class CsrUpdateValidatorImpl implements CsrUpdateValidator {
    private CsrUpdateRepository csrUpdateRepository
    private UserResource userResource
    private Integer updateMaxLength

    @Required
    void setCsrUpdateRepository(CsrUpdateRepository csrUpdateRepository) {
        this.csrUpdateRepository = csrUpdateRepository
    }

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Required
    void setUpdateMaxLength(Integer updateMaxLength) {
        this.updateMaxLength = updateMaxLength
    }

    @Override
    Promise<CsrUpdate> validateForGet(CsrUpdateId csrUpdateId) {
        if (csrUpdateId == null || csrUpdateId.value == null) {
            throw new IllegalArgumentException('csrUpdateId is null')
        }

        return csrUpdateRepository.get(csrUpdateId).then { CsrUpdate csrUpdate ->
            if (csrUpdate == null) {
                throw AppErrors.INSTANCE.csrUpdateNotFound(csrUpdateId).exception()
            }

            return Promise.pure(csrUpdate)
        }
    }

    @Override
    Promise<Void> validateForSearch(CsrUpdateListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('csr update listOptions is null')
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(CsrUpdate csrUpdate) {
        if (csrUpdate == null) {
            throw new IllegalArgumentException('csrUpdate is null')
        }

        if (csrUpdate.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }

        return this.checkBasicInfo(csrUpdate).then {
            return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateForUpdate(CsrUpdateId csrUpdateId, CsrUpdate csrUpdate, CsrUpdate oldCsrUpdate) {
        if (csrUpdateId == null) {
            throw new IllegalArgumentException('csrUpdateId is null')
        }

        if (csrUpdate == null) {
            throw new IllegalArgumentException('csrUpdate is null')
        }

        if (csrUpdateId != csrUpdate.id) {
            throw AppErrors.INSTANCE.fieldInvalid('id').exception()
        }

        if (csrUpdateId != oldCsrUpdate.id) {
            throw AppErrors.INSTANCE.fieldInvalid('id').exception()
        }

        return this.checkBasicInfo(csrUpdate).then {
            return Promise.pure(null)
        }
    }

    @Override
    Promise<CsrUpdate> validateForPatch(CsrUpdateId csrUpdateId, CsrUpdate csrUpdate, CsrUpdate oldCsrUpdate) {
        if (csrUpdateId == null) {
            throw new IllegalArgumentException('csrUpdateId is null')
        }

        if (csrUpdate == null) {
            throw new IllegalArgumentException('csrUpdate is null')
        }

        if (csrUpdateId != csrUpdate.id) {
            throw AppErrors.INSTANCE.fieldInvalid('id').exception()
        }

        if (csrUpdateId != oldCsrUpdate.id) {
            throw AppErrors.INSTANCE.fieldInvalid('id').exception()
        }

        CsrUpdate newCsrUpdate = oldCsrUpdate
        if (csrUpdate.content != null) {
            newCsrUpdate.content = csrUpdate.content
        }
        if (csrUpdate.userId != null) {
            newCsrUpdate.userId = csrUpdate.userId
        }
        if (csrUpdate.active != null) {
            newCsrUpdate.active = csrUpdate.active
        }

        return this.checkBasicInfo(newCsrUpdate).then {
            return Promise.pure(newCsrUpdate)
        }
    }

    private Promise<Void> checkBasicInfo(CsrUpdate csrUpdate) {
        if (csrUpdate == null) {
            throw new IllegalArgumentException('csrUpdate is null')
        }

        if (csrUpdate.content == null) {
            throw AppErrors.INSTANCE.fieldRequired('content').exception()
        }
        else if (csrUpdate.content.length() > this.updateMaxLength) {
            throw AppErrors.INSTANCE.fieldInvalid('content').exception()
        }

        if (csrUpdate.active == null) {
            throw AppErrors.INSTANCE.fieldRequired('active').exception()
        }

        if (csrUpdate.userId == null) {
            throw AppErrors.INSTANCE.fieldRequired('userId').exception()
        }
        else {
            return userResource.get(csrUpdate.userId, new UserGetOptions()).then { User user ->
                if (user == null) {
                    throw AppErrors.INSTANCE.userNotFound().exception()
                }

                csrUpdate.username = user.username

                return Promise.pure(null)
            }
        }
    }
}
