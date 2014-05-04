/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.clientproxy.impl.mandrill

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic

/**
 * Response of Mandrill.
 */
@CompileStatic
class MandrillResponse {
    @JsonProperty('status')
    String status

    @JsonProperty('_id')
    String id

    @JsonProperty('email')
    String email

    @JsonProperty('reject_reason')
    String reason

    @JsonIgnore
    String body

    @JsonIgnore
    int code

    String getStatus() {
        return status
    }

    void setStatus(String status) {
        this.status = status
    }

    String getId() {
        return id
    }

    void setId(String id) {
        this.id = id
    }

    String getEmail() {
        return email
    }

    void setEmail(String email) {
        this.email = email
    }

    String getReason() {
        return reason
    }

    void setReason(String reason) {
        this.reason = reason
    }

    String getBody() {
        return body
    }

    void setBody(String body) {
        this.body = body
    }

    int getCode() {
        return code
    }

    void setCode(int code) {
        this.code = code
    }
}
