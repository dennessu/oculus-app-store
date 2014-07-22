package com.junbo.csr.db.repo.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.csr.db.generator.TokenGenerator
import com.junbo.csr.db.repo.CsrInvitationCodeRepository
import com.junbo.csr.spec.model.CsrInvitationCode
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 7/16/14.
 */
@CompileStatic
class CsrInvitationCodeRepositoryCloudantImpl extends CloudantClient<CsrInvitationCode> implements CsrInvitationCodeRepository {
    private TokenGenerator tokenGenerator

    @Required
    void setTokenGenerator(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator
    }

    @Override
    CsrInvitationCode getAndRemove(String code) {
        CsrInvitationCode entity = cloudantGetSync(code)
        cloudantDeleteSync(code)
        return entity
    }

    @Override
    void save(CsrInvitationCode csrInvitationCode) {
        if (csrInvitationCode.code == null) {
            csrInvitationCode.code = tokenGenerator.generateCsrInvitationCode()
        }
        cloudantPostSync(csrInvitationCode)
    }

    @Override
    void removeByUserIdEmail(Long userId, String email) {
        List<CsrInvitationCode> entities = queryViewSync('by_user_id_email', "$userId:$email")
        for (CsrInvitationCode code : entities) {
            cloudantDeleteSync(code)
        }
    }
}
