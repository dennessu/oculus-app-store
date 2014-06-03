/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.sabrix

import com.thoughtworks.xstream.annotations.XStreamAlias
import groovy.transform.CompileStatic

/**
 * Response of Sabrix address validation service.
 */
@CompileStatic
@XStreamAlias("ADDRESS_VALIDATION_RESPONSE")
class AddressValidationResponse {
    @XStreamAlias('ADDRESS')
    List<ResponseAddress> address

    @XStreamAlias('MESSAGE')
    List<Message> message

    @Override
    public String toString() {
        return "AddressValidationResponse{" +
                "address=" + address +
                ", message=" + message +
                '}';
    }
}
