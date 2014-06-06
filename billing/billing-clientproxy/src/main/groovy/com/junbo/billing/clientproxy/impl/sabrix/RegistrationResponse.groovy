/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.sabrix

import com.thoughtworks.xstream.annotations.XStreamAlias
import groovy.transform.CompileStatic

/**
 * Registration response for VAT ID validation.
 */
@CompileStatic
@XStreamAlias("REGISTRATION_RESPONSE")
class RegistrationResponse {
    @XStreamAlias('REGISTRATION')
    String registration

    @XStreamAlias('MESSAGE')
    String message

    @XStreamAlias('NAME')
    String name

    @XStreamAlias('ADDRESS')
    String address

    @XStreamAlias('REQUEST_DATE')
    String requestDate


    @Override
    public String toString() {
        return "RegistrationResponse{" +
                "registration='" + registration + '\'' +
                ", message='" + message + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", requestDate='" + requestDate + '\'' +
                '}';
    }
}
