package com.junbo.identity.core.service.normalize.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.identity.core.service.normalize.NormalizeService
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Created by liangfu on 4/10/14.
 */
@CompileStatic
class NormalizeServiceImpl implements NormalizeService {

    private String charsToDelete

    @Required
    void setCharsToDelete(String charsToDelete) {
        this.charsToDelete = charsToDelete
    }

    @Override
    String normalize(String name) {
        if (name == null) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('name', 'name is null').exception()
        }

        def result = StringUtils.deleteAny(name, charsToDelete)

        return result.toLowerCase(Locale.ENGLISH)
    }
}
