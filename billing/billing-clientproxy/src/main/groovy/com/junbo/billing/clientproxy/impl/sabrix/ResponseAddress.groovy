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
 * Address Type of address validation response.
 */
@CompileStatic
@XStreamAlias("ADDRESS")
class ResponseAddress {
    @XStreamAsAttribute
    @XStreamAlias('COUNTRY')
    Country country

    @XStreamAsAttribute
    @XStreamAlias('PROVINCE')
    Province province

    @XStreamAsAttribute
    @XStreamAlias('STATE')
    State state

    @XStreamAsAttribute
    @XStreamAlias('COUNTY')
    County county

    @XStreamAsAttribute
    @XStreamAlias('CITY')
    City city

    @XStreamAsAttribute
    @XStreamAlias('POSTCODE')
    Postcode postcode


    @Override
    public String toString() {
        return "ResponseAddress{" +
                "country=" + country.toString() +
                ", province=" + province.toString() +
                ", state=" + state.toString() +
                ", county=" + county.toString() +
                ", city=" + city.toString() +
                ", postcode=" + postcode.toString() +
                '}';
    }
}
