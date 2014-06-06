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
 * Registration validation request for VAT ID validation.
 */
@CompileStatic
@XStreamAlias("REGISTRATION_VALIDATION_REQUEST")
class RegistrationValidationRequest {
    @XStreamImplicit(itemFieldName="REGISTRATION")
    List<String> registration


    @Override
    public String toString() {
        return "RegistrationValidationRequest{" +
                "registration=" + registration +
                '}';
    }
}
