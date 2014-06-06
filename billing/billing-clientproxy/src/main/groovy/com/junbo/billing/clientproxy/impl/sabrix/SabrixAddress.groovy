/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.sabrix

import com.thoughtworks.xstream.annotations.XStreamAlias
import groovy.transform.CompileStatic

/**
 * Address Type of Sabrix tax engine.
 */
@CompileStatic
@XStreamAlias('ADDRESS')
class SabrixAddress {
    @XStreamAlias('COUNTRY')
    String country

    @XStreamAlias('STATE')
    String state

    @XStreamAlias('PROVINCE')
    String province

    @XStreamAlias('COUNTY')
    String county

    @XStreamAlias('CITY')
    String city

    @XStreamAlias('POSTCODE')
    String postcode

    @Override
    public String toString() {
        return "SabrixAddress{" +
                "country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", province='" + province + '\'' +
                ", county='" + county + '\'' +
                ", city='" + city + '\'' +
                ", postcode='" + postcode + '\'' +
                '}';
    }
}
