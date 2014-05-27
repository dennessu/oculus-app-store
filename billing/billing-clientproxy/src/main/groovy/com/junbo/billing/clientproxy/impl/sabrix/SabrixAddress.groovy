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
 * Address Type of Sabrix tax engine.
 */
@CompileStatic
@XStreamAlias("ADDRESS")
class SabrixAddress {
    @XStreamAsAttribute
    @XStreamAlias('COUNTRY')
    String country

    @XStreamAsAttribute
    @XStreamAlias('STATE')
    String state

    @XStreamAsAttribute
    @XStreamAlias('PROVINCE')
    String province

    @XStreamAsAttribute
    @XStreamAlias('COUNTY')
    String county

    @XStreamAsAttribute
    @XStreamAlias('CITY')
    String city

    @XStreamAsAttribute
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
