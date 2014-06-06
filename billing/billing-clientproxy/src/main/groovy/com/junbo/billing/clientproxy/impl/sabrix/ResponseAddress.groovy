/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.sabrix

import com.thoughtworks.xstream.annotations.XStreamAlias
import groovy.transform.CompileStatic

/**
 * Address Type of address validation response.
 */
@CompileStatic
@XStreamAlias('ADDRESS')
class ResponseAddress {
    @XStreamAlias('COUNTRY')
    Country country

    @XStreamAlias('PROVINCE')
    Province province

    @XStreamAlias('STATE')
    State state

    @XStreamAlias('COUNTY')
    County county

    @XStreamAlias('CITY')
    City city

    @XStreamAlias('POSTCODE')
    Postcode postcode


    @Override
    public String toString() {
        return "ResponseAddress{" +
                "country=" + country +
                ", province=" + province +
                ", state=" + state +
                ", county=" + county +
                ", city=" + city +
                ", postcode=" + postcode +
                '}';
    }
}
