package com.junbo.oauth.db.repo

import com.junbo.oauth.spec.model.ResetPasswordCode
import groovy.transform.CompileStatic

/**
 * Created by minhao on 5/2/14.
 */
@CompileStatic
public interface ResetPasswordCodeRepository {
    ResetPasswordCode getAndRemove(String code)
    void save(ResetPasswordCode resetPasswordCode)
    void removeByUserIdEmail(Long userId, String email)
}