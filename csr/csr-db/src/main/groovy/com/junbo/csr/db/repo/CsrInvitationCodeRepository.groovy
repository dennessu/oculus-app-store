package com.junbo.csr.db.repo

import com.junbo.csr.spec.model.CsrInvitationCode

/**
 * Created by haomin on 7/16/14.
 */
public interface CsrInvitationCodeRepository {
    CsrInvitationCode getAndRemove(String code)

    void save(CsrInvitationCode emailVerifyCode)

    void removeByUserIdEmail(Long userId, String email)
}