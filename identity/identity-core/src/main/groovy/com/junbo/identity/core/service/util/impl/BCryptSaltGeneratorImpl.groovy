package com.junbo.identity.core.service.util.impl

import com.junbo.identity.core.service.util.SaltGenerator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.security.crypto.bcrypt.BCrypt

/**
 * Created by liangfu on 5/15/14.
 */
@CompileStatic
class BCryptSaltGeneratorImpl implements SaltGenerator {

    private Integer logRound

    @Override
    String generateSalt() {
        return BCrypt.gensalt(logRound)
    }

    @Required
    void setLogRound(Integer logRound) {
        this.logRound = logRound
    }
}
