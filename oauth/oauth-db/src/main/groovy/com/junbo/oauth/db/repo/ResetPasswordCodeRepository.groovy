package com.junbo.oauth.db.repo

import com.junbo.oauth.spec.model.ResetPasswordCode
import groovy.transform.CompileStatic

/**
 * Created by minhao on 5/2/14.
 */
@CompileStatic
public interface ResetPasswordCodeRepository {
    ResetPasswordCode get(String code)
    ResetPasswordCode getByHash(String hash, Integer dc)
    void remove(String code)
    void removeByHash(String hash)
    void save(ResetPasswordCode resetPasswordCode)
    void removeByUserIdEmail(Long userId, String email)
    List<ResetPasswordCode> getByUserIdEmail(Long userId, String email)
}