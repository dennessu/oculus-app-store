package com.junbo.oauth.spec.model

import groovy.transform.CompileStatic

/**
 * Created by minhao on 5/1/14.
 */
@CompileStatic
class ResetPasswordCode {
    String code
    String email
    Long userId
}
