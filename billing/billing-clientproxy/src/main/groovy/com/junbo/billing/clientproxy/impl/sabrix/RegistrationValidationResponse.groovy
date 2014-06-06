/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.sabrix

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamImplicit
import groovy.transform.CompileStatic

/**
 * Registration validation response for VAT ID validation.
 */
@CompileStatic
@XStreamAlias("REGISTRATION_VALIDATION_RESPONSE")
class RegistrationValidationResponse {
    @XStreamImplicit(itemFieldName="REGISTRATION_RESPONSE")
    List<RegistrationResponse> registration


    @Override
    public String toString() {
        return "RegistrationValidationResponse{" +
                "registration=" + registration +
                '}';
    }
}
