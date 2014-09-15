package com.junbo.oauth.spec.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.junbo.common.cloudant.json.annotations.CloudantIgnore
import com.junbo.common.model.ResourceMeta
import groovy.transform.CompileStatic

/**
 * Created by minhao on 5/1/14.
 */
@CompileStatic
class ResetPasswordCode extends ResourceMeta<String> {
    @CloudantIgnore
    String code

    @JsonIgnore
    String hashedCode
    String email
    Long userId

    @Override
    String getId() {
        return hashedCode
    }

    @Override
    void setId(String id) {
        this.hashedCode = id
    }
}
