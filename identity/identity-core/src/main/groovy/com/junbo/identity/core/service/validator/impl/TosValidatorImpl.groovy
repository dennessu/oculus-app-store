package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.TosId
import com.junbo.identity.core.service.validator.TosValidator
import com.junbo.identity.data.repository.TosRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Tos
import com.junbo.identity.spec.v1.option.list.TosListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/9/14.
 */
@CompileStatic
class TosValidatorImpl implements TosValidator {

    private TosRepository tosRepository

    private List<String> allowedLocales

    private Integer titleMinLength
    private Integer titleMaxLength

    private Integer contentMinLength
    private Integer contentMaxLength

    @Override
    Promise<Tos> validateForGet(TosId tosId) {
        if (tosId == null || tosId.value == null) {
            throw new IllegalArgumentException('tosId is null')
        }

        return tosRepository.get(tosId).then { Tos tos ->
            if (tos == null) {
                throw AppErrors.INSTANCE.tosNotFound(tosId).exception()
            }

            return Promise.pure(tos)
        }
    }

    @Override
    Promise<Void> validateForSearch(TosListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(Tos tos) {
        checkBasicTosInfo(tos)

        if (tos.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }
        return Promise.pure(null)
    }

    private void checkBasicTosInfo(Tos tos) {
        if (tos == null) {
            throw new IllegalArgumentException('tos is null')
        }

        if (tos.locale == null) {
            throw AppErrors.INSTANCE.fieldRequired('locale').exception()
        }
        if (!(tos.locale in allowedLocales)) {
            throw AppErrors.INSTANCE.fieldInvalid('locale', allowedLocales.join(',')).exception()
        }

        if (tos.title == null) {
            throw AppErrors.INSTANCE.fieldRequired('title').exception()
        }
        if (tos.title.size() > titleMaxLength) {
            throw AppErrors.INSTANCE.fieldTooLong('title', titleMaxLength).exception()
        }
        if (tos.title.size() < titleMinLength) {
            throw AppErrors.INSTANCE.fieldTooShort('title', titleMinLength).exception()
        }

        if (tos.content == null) {
            throw AppErrors.INSTANCE.fieldRequired('content').exception()
        }
        if (tos.content.size() > contentMaxLength) {
            throw AppErrors.INSTANCE.fieldTooLong('content', contentMaxLength).exception()
        }
        if (tos.content.size() < contentMinLength) {
            throw AppErrors.INSTANCE.fieldTooShort('content', contentMinLength).exception()
        }
    }

    @Required
    void setTosRepository(TosRepository tosRepository) {
        this.tosRepository = tosRepository
    }

    @Required
    void setAllowedLocales(List<String> allowedLocales) {
        this.allowedLocales = allowedLocales
    }

    @Required
    void setTitleMinLength(Integer titleMinLength) {
        this.titleMinLength = titleMinLength
    }

    @Required
    void setTitleMaxLength(Integer titleMaxLength) {
        this.titleMaxLength = titleMaxLength
    }

    @Required
    void setContentMinLength(Integer contentMinLength) {
        this.contentMinLength = contentMinLength
    }

    @Required
    void setContentMaxLength(Integer contentMaxLength) {
        this.contentMaxLength = contentMaxLength
    }
}
