package com.junbo.oauth.db.entity

import groovy.transform.CompileStatic

/**
 * Created by minhao on 5/2/14.
 */
@CompileStatic
class ResetPasswordCodeEntity extends BaseEntity {
    Long userId
    String email
}
