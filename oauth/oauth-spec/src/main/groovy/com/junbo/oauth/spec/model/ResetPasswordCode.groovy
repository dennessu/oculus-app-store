package com.junbo.oauth.spec.model

import com.junbo.common.model.ResourceMeta
import groovy.transform.CompileStatic

/**
 * Created by minhao on 5/1/14.
 */
@CompileStatic
class ResetPasswordCode extends ResourceMeta<String> {
    String code
    String email
    Long userId

    @Override
    String getId() {
        return code
    }

    @Override
    void setId(String id) {
        this.code = id
    }
}
