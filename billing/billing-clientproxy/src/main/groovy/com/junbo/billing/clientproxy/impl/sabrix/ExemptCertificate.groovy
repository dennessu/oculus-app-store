/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.sabrix

import com.thoughtworks.xstream.annotations.XStreamAlias
import groovy.transform.CompileStatic

/**
 * Exempt certificate for tax calculation.
 */
@CompileStatic
@XStreamAlias('EXEMPT_CERTIFICATE')
class ExemptCertificate {
    @XStreamAlias('COUNTRY')
    String country

    @XStreamAlias('PROVINCE')
    String province

    @XStreamAlias('STATE')
    String state

    @XStreamAlias('COUNTY')
    String county

    @XStreamAlias('CITY')
    String city

    @XStreamAlias('DISTRICT')
    String district

    @XStreamAlias('POSTCODE')
    String postcode

    @XStreamAlias('GEOCODE')
    String geocode

    @Override
    public String toString() {
        return "ExemptCertificate{" +
                "country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", state='" + state + '\'' +
                ", county='" + county + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", postcode='" + postcode + '\'' +
                ", geocode='" + geocode + '\'' +
                '}';
    }
}
