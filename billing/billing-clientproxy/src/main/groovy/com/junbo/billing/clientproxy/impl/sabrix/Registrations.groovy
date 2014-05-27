/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.sabrix

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import groovy.transform.CompileStatic

/**
 * Registrations for tax calculation.
 */
@CompileStatic
@XStreamAlias("REGISTRATIONS")
class Registrations {
    @XStreamAsAttribute
    @XStreamAlias('BUYER_ROLE')
    String buyerRole

    @XStreamAsAttribute
    @XStreamAlias('SELLER_ROLE')
    String sellerRole


    @Override
    public String toString() {
        return "Registrations{" +
                "buyerRole='" + buyerRole + '\'' +
                ", sellerRole='" + sellerRole + '\'' +
                '}';
    }
}
